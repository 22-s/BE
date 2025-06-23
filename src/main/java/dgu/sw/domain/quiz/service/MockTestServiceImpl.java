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
    public List<MockTestResultResponse> getAllMockTestResults(String userId) {
        Long uid = Long.valueOf(userId);

        // 사용자의 모든 완료된 모의고사 조회 (정렬 포함)
        List<MockTest> mockTests = mockTestRepository
                .findByUser_UserIdAndIsCompletedTrueOrderByMockTestIdAsc(uid);

        List<MockTestResultResponse> results = mockTests.stream().map(mockTest -> {
            Long mockTestId = mockTest.getMockTestId();
            List<MockTestQuiz> quizzes = mockTestQuizRepository.findByMockTest_MockTestId(mockTestId);

            // 카테고리별 정답 수 계산
            Map<String, Long> totalByCategory = quizzes.stream()
                    .collect(Collectors.groupingBy(q -> q.getQuiz().getCategory(), Collectors.counting()));

            Map<String, Long> correctByCategory = quizzes.stream()
                    .filter(MockTestQuiz::isCorrect)
                    .collect(Collectors.groupingBy(q -> q.getQuiz().getCategory(), Collectors.counting()));

            List<MockTestResultResponse.CategoryResult> categoryResults = totalByCategory.entrySet().stream()
                    .map(entry -> MockTestResultResponse.CategoryResult.builder()
                            .category(entry.getKey())
                            .totalCount(entry.getValue().intValue())
                            .correctCount(correctByCategory.getOrDefault(entry.getKey(), 0L).intValue())
                            .build())
                    .collect(Collectors.toList());

            // 문제별 결과
            List<MockTestResultResponse.MockTestQuestionResult> questionResults = quizzes.stream()
                    .map(q -> MockTestResultResponse.MockTestQuestionResult.builder()
                            .quizId(q.getQuiz().getQuizId())
                            .category(q.getQuiz().getCategory())
                            .question(q.getQuiz().getQuestion())
                            .isCorrect(q.isCorrect())
                            .build())
                    .collect(Collectors.toList());

            // 회차 정보 (몇 번째인지)
            int attemptCount = mockTests.indexOf(mockTest) + 1;

            // 점수
            int score = (int) ((double) mockTest.getCorrectCount() / quizzes.size() * 100);

            // 이전 모의고사 점수 및 퍼센트 변화 계산
            int scoreChange = 0;
            double topPercentileChange = 0.0;

            if (attemptCount > 1) {
                MockTest previous = mockTests.get(attemptCount - 2);
                List<MockTestQuiz> prevQuizzes = mockTestQuizRepository.findByMockTest_MockTestId(previous.getMockTestId());
                int previousScore = (int) ((double) previous.getCorrectCount() / prevQuizzes.size() * 100);
                scoreChange = score - previousScore;
                topPercentileChange = previous.getTopPercentile() - mockTest.getTopPercentile();
            }

            else {
                scoreChange = score;
                topPercentileChange = mockTest.getTopPercentile();
            }

            return MockTestConverter.toMockTestResultResponse(
                    mockTest,
                    attemptCount,
                    score,
                    scoreChange,
                    topPercentileChange,
                    categoryResults,
                    questionResults
            );

        }).collect(Collectors.toList());

        return results;
    }
}