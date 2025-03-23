package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.converter.MockTestConverter;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.MockTestResultResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.MockTestResultResponse.MockTestQuestionResult;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.SubmitMockTestResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestRequest.SubmitMockTestRequest;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.CreateMockTestResponse;
import dgu.sw.domain.quiz.entity.MockTest;
import dgu.sw.domain.quiz.entity.MockTestQuiz;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.repository.MockTestQuizRepository;
import dgu.sw.domain.quiz.repository.MockTestRepository;
import dgu.sw.domain.quiz.repository.QuizRepository;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.exception.QuizException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MockTestServiceImpl implements MockTestService {
    private final MockTestRepository mockTestRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final MockTestQuizRepository mockTestQuizRepository;

    @Override
    @Transactional
    public CreateMockTestResponse startMockTest(String userId) {
        User user = userRepository.findByUserId(Long.valueOf(userId));

        // 랜덤한 10문제 가져오기
        List<Quiz> randomQuizzes = quizRepository.findRandomQuizzes(10);

        // 모의고사 생성
        MockTest mockTest = MockTest.builder()
                .user(user)
                .createdDate(LocalDate.now())
                .isCompleted(false)
                .correctCount(0)
                .build();
        mockTestRepository.save(mockTest);

        // 모의고사 문제 저장
        List<MockTestQuiz> mockTestQuizzes = randomQuizzes.stream()
                .map(quiz -> MockTestQuiz.builder()
                        .mockTest(mockTest)
                        .quiz(quiz)
                        .isCorrect(false) // 초기값 설정
                        .build())
                .collect(Collectors.toList());

        mockTestQuizRepository.saveAll(mockTestQuizzes);

        return MockTestConverter.toCreateMockTestResponse(mockTest, mockTestQuizzes);
    }

    @Override
    @Transactional
    public SubmitMockTestResponse submitMockTest(Long mockTestId, SubmitMockTestRequest request) {
        MockTest mockTest = mockTestRepository.findById(mockTestId)
                .orElseThrow(() -> new QuizException(ErrorStatus.QUIZ_NOT_FOUND));

        List<MockTestQuiz> mockTestQuizzes = mockTestQuizRepository.findByMockTest_MockTestId(mockTestId);

        int correctCount = 0;
        for (SubmitMockTestRequest.Answer answer : request.getAnswers()) {
            MockTestQuiz quizRecord = mockTestQuizzes.stream()
                    .filter(mtq -> mtq.getQuiz().getQuizId().equals(answer.getQuizId()))
                    .findFirst()
                    .orElseThrow(() -> new QuizException(ErrorStatus.QUIZ_NOT_FOUND));

            boolean isCorrect = quizRecord.getQuiz().getAnswer().equals(answer.getSelectedAnswer());
            quizRecord.updateCorrect(isCorrect);
            if (isCorrect) correctCount++;
        }

        // 현재 사용자 기준 이전까지 본 모의고사 평균 점수 계산
        Long userId = mockTest.getUser().getUserId();
        List<MockTest> userTests = mockTestRepository.findByUser_UserIdAndMockTestIdLessThan(userId, mockTestId);
        double myAverage = userTests.stream()
                .mapToDouble(mt -> ((double) mt.getCorrectCount() / mt.getMockTestQuizzes().size()) * 100)
                .average().orElse(0.0);

        // 전체 사용자들의 평균 점수 계산
        List<MockTest> completedTests = mockTestRepository.findAllByIsCompletedTrue();
        List<Double> allUserAverageScores = completedTests.stream()
                .filter(mt -> mt.getMockTestId() < mockTestId)
                .collect(Collectors.groupingBy(MockTest::getUser, Collectors.averagingDouble(
                        mt -> ((double) mt.getCorrectCount() / mt.getMockTestQuizzes().size()) * 100)))
                .values()
                .stream()
                .collect(Collectors.toList());

        // 내 평균 점수 포함 후 정렬
        allUserAverageScores.add(myAverage);
        allUserAverageScores.sort(Comparator.reverseOrder());

        int myRank = allUserAverageScores.indexOf(myAverage) + 1;
        double topPercentile = allUserAverageScores.size() > 0 ? ((double) myRank / allUserAverageScores.size()) * 100 : 0.0;

        // 완료 및 저장
        mockTest.updateCompleted(true, correctCount, topPercentile);
        mockTestRepository.save(mockTest);

        return MockTestConverter.toSubmitMockTestResponse(mockTest, mockTestQuizzes, request.getAnswers());
    }

    @Override
    public MockTestResultResponse getMockTestResult(Long mockTestId) {
        // 1. 모의고사 정보 가져오기
        MockTest mockTest = mockTestRepository.findById(mockTestId)
                .orElseThrow(() -> new QuizException(ErrorStatus.MOCK_TEST_NOT_COMPLETED));

        List<MockTestQuiz> mockTestQuizzes = mockTestQuizRepository.findByMockTest_MockTestId(mockTestId);

        // 2. 전체 문제 정보 가공
        List<MockTestQuestionResult> questionResults = mockTestQuizzes.stream()
                .map(mtq -> MockTestResultResponse.MockTestQuestionResult.builder()
                        .quizId(mtq.getQuiz().getQuizId())
                        .category(mtq.getQuiz().getCategory())
                        .question(mtq.getQuiz().getQuestion())
                        .isCorrect(mtq.isCorrect())
                        .build())
                .collect(Collectors.toList());

        // 3. 카테고리별 정답 개수 계산
        Map<String, Long> totalQuestionsByCategory = mockTestQuizzes.stream()
                .collect(Collectors.groupingBy(mtq -> mtq.getQuiz().getCategory(), Collectors.counting()));

        Map<String, Long> correctQuestionsByCategory = mockTestQuizzes.stream()
                .filter(MockTestQuiz::isCorrect)
                .collect(Collectors.groupingBy(mtq -> mtq.getQuiz().getCategory(), Collectors.counting()));

        List<MockTestResultResponse.CategoryResult> categoryResults = totalQuestionsByCategory.entrySet().stream()
                .map(entry -> MockTestResultResponse.CategoryResult.builder()
                        .category(entry.getKey())
                        .correctCount(correctQuestionsByCategory.getOrDefault(entry.getKey(), 0L).intValue())
                        .totalCount(entry.getValue().intValue())
                        .build())
                .collect(Collectors.toList());

        // 4. 해당 모의고사가 몇 번째인지 계산
        List<MockTest> userMockTests = mockTestRepository.findByUser_UserIdOrderByCreatedDateAsc(mockTest.getUser().getUserId());
        int attemptCount = userMockTests.indexOf(mockTest) + 1; // 1부터 시작하는 순서

        // 현재 점수 계산
        int score = (int) ((double) mockTest.getCorrectCount() / mockTestQuizzes.size() * 100);

        // 현재 모의고사 정보
        Long currentMockTestId = mockTest.getMockTestId();
        Long userId = mockTest.getUser().getUserId();

        // 현재 모의고사를 푼 유저가 푼 mockTestId 중 현재 mockTestId보다 작은 것들 중 가장 큰 것
        Optional<MockTest> previousMockTestOpt = mockTestRepository.findTopByUser_UserIdAndMockTestIdLessThanOrderByMockTestIdDesc(userId, currentMockTestId);

        // 이전 모의고사 점수 계산
        int scoreChange = 0;

        if (previousMockTestOpt.isPresent()) {
            MockTest previousMockTest = previousMockTestOpt.get();
            List<MockTestQuiz> previousQuizzes = mockTestQuizRepository.findByMockTest_MockTestId(previousMockTest.getMockTestId());
            int previousScore = previousQuizzes.size() > 0
                    ? (int) ((double) previousMockTest.getCorrectCount() / previousQuizzes.size() * 100)
                    : 0;
            scoreChange = score - previousScore;
        }

        // 상위 퍼센트 계산 (점수가 높을수록 0에 가까워짐)
        double topPercentile = mockTest.getTopPercentile();
        double previousTopPercentile = previousMockTestOpt
                .map(MockTest::getTopPercentile)
                .orElse(topPercentile);

        double topPercentileChange = previousTopPercentile - topPercentile;

        // 7. 최종 결과 반환
        return MockTestResultResponse.builder()
                .mockTestId(mockTestId)
                .score(score)
                .attemptCount(attemptCount)
                .scoreChange(scoreChange)
                .topPercentile(topPercentile)
                .topPercentileChange(topPercentileChange)
                .categoryResults(categoryResults)
                .questionResults(questionResults)
                .build();
    }

    @Override
    public MockTestResultResponse getPreviousMockTestResult(String userId) {
        Long uid = Long.valueOf(userId);

        // 유저가 푼 가장 최근(제일 마지막) 이전 모의고사
        Optional<MockTest> previousMockTestOpt = mockTestRepository
                .findTopByUser_UserIdAndIsCompletedTrueOrderByCreatedDateDesc(uid);

        MockTest mockTest = previousMockTestOpt
                .orElseThrow(() -> new QuizException(ErrorStatus.MOCK_TEST_NOT_FOUND));

        return getMockTestResult(mockTest.getMockTestId());
    }

}