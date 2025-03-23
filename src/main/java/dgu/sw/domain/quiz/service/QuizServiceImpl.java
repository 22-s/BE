package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.converter.QuizConverter;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.YesterdayQuizResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizMainPageResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizSearchResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizReviewResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizListResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizDetailResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizResultResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizRequest.SubmitQuizRequest;
import dgu.sw.domain.quiz.entity.MockTest;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.entity.QuizReviewList;
import dgu.sw.domain.quiz.entity.UserQuiz;
import dgu.sw.domain.quiz.repository.MockTestRepository;
import dgu.sw.domain.quiz.repository.QuizRepository;
import dgu.sw.domain.quiz.repository.QuizReviewListRepository;
import dgu.sw.domain.quiz.repository.UserQuizRepository;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.exception.QuizException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final UserQuizRepository userQuizRepository;
    private final QuizReviewListRepository quizReviewListRepository;
    private final UserRepository userRepository;
    private final MockTestRepository mockTestRepository;

    @Override
    public List<QuizListResponse> getQuizList(String userId, int category) {
        // int 카테고리를 String으로 매핑
        String categoryName = mapCategoryToString(category);

        List<Quiz> quizzes = quizRepository.findByCategory(categoryName);

        if (quizzes.isEmpty()) {
            throw new QuizException(ErrorStatus.QUIZ_SEARCH_NO_RESULTS);
        }

        // 사용자 관련 데이터를 조회
        List<UserQuiz> userQuizzes = userQuizRepository.findByUser_UserId(Long.valueOf(userId));
        Map<Long, UserQuiz> userQuizMap = userQuizzes.stream()
                .collect(Collectors.toMap(userQuiz -> userQuiz.getQuiz().getQuizId(), userQuiz -> userQuiz));

        List<QuizReviewList> reviewList = quizReviewListRepository.findByUser_UserId(Long.valueOf(userId));
        Map<Long, QuizReviewList> reviewQuizMap = reviewList.stream()
                .collect(Collectors.toMap(quizReview -> quizReview.getQuiz().getQuizId(), quizReview -> quizReview));

        // 첫 번째 퀴즈 잠금 해제 처리
        boolean[] unlockedFirstQuiz = {false};

        return quizzes.stream()
                .map(quiz -> {
                    Long quizId = quiz.getQuizId();

                    // 이미 풀었는지 확인
                    boolean isSolved = userQuizMap.containsKey(quizId);

                    // 복습 리스트 포함 여부 확인
                    boolean isInReviewList = reviewQuizMap.containsKey(quizId);

                    // 정답 여부 확인
                    boolean isCorrect = userQuizMap.containsKey(quizId) && userQuizMap.get(quizId).isCorrect();

                    // 잠금 여부 처리
                    boolean isLocked;
                    if (!isSolved && !unlockedFirstQuiz[0]) {
                        unlockedFirstQuiz[0] = true; // 첫 번째 잠금 해제 퀴즈를 처리
                        isLocked = false;
                    } else {
                        isLocked = !isSolved;
                    }

                    return QuizConverter.toQuizListResponse(quiz, isLocked, isSolved, isInReviewList, isCorrect);
                })
                .collect(Collectors.toList());
    }

    private String mapCategoryToString(int category) {
        switch (category) {
            case 1:
                return "기본 매너";
            case 2:
                return "명함 공유 매너";
            case 3:
                return "직장인 글쓰기 Tip";
            case 4:
                return "팀장님께 메일 보내기";
            case 5:
                return "커뮤니케이션 매너";
            case 6:
                return "TPO에 맞는 복장";
            default:
                throw new IllegalArgumentException("잘못된 카테고리 번호입니다: " + category);
        }
    }

    @Override
    public QuizDetailResponse getQuizDetail(String userId, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizException(ErrorStatus.QUIZ_NOT_FOUND));

        // 사용자가 해당 퀴즈를 풀었는지 확인
        boolean isSolved = userQuizRepository.findByUser_UserIdAndQuiz_QuizId(Long.valueOf(userId), quizId).isPresent();

        // 복습 리스트에 포함 여부 확인
        boolean isInReviewList = quizReviewListRepository.existsByUser_UserIdAndQuiz_QuizId(Long.valueOf(userId), quizId);

        return QuizConverter.toQuizDetailResponse(quiz, isSolved, isInReviewList);
    }

    @Override
    public QuizResultResponse submitQuizAnswer(String userId, Long quizId, SubmitQuizRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizException(ErrorStatus.QUIZ_NOT_FOUND));

        Long uid = Long.valueOf(userId);
        String category = quiz.getCategory();

        // 해당 카테고리에서 가장 먼저 풀어야 하는 퀴즈 ID 찾기
        Long firstQuizId = quizRepository.findFirstQuizIdByCategory(category);

        // 사용자가 풀려는 퀴즈의 바로 이전 퀴즈 찾기
        Long previousQuizId = quizRepository.findPreviousQuizId(category, quizId);

        // 이전 퀴즈를 풀었는지 확인 (첫 퀴즈가 아닐 경우만 체크)
        if (!quizId.equals(firstQuizId)) {
            boolean previousSolved = userQuizRepository.existsByUser_UserIdAndQuiz_QuizId(uid, previousQuizId);
            if (!previousSolved) {
                throw new QuizException(ErrorStatus.QUIZ_LOCKED);
            }
        }

        boolean isCorrect = quiz.getAnswer().equals(request.getSelectedAnswer());

        UserQuiz existingUserQuiz = userQuizRepository.findByUser_UserIdAndQuiz_QuizId(Long.valueOf(userId), quizId)
                .orElse(null);

        if (existingUserQuiz != null) {
            existingUserQuiz.updateCorrect(isCorrect);

            // retriedToday 체크
            boolean solvedYesterday = userQuizRepository.existsByUser_UserIdAndQuiz_QuizIdAndSolvedDate(
                    Long.valueOf(userId), quizId, LocalDate.now().minusDays(1)
            );
            existingUserQuiz.updateRetriedToday(solvedYesterday);

            userQuizRepository.save(existingUserQuiz);
        }
        else {
            User user = userRepository.findByUserId(Long.valueOf(userId));
            UserQuiz userQuiz = QuizConverter.toUserQuiz(user, quiz, isCorrect);

            userQuizRepository.save(userQuiz);
        }

        return QuizConverter.toQuizResultResponse(isCorrect);
    }

    @Override
    public void addQuizToReview(String userId, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizException(ErrorStatus.QUIZ_NOT_FOUND));

        boolean alreadyInReview = quizReviewListRepository.existsByUser_UserIdAndQuiz_QuizId(Long.valueOf(userId), quizId);

        // 사용자가 해당 퀴즈를 풀었는지 확인
        UserQuiz userQuiz = userQuizRepository.findByUser_UserIdAndQuiz_QuizId(Long.valueOf(userId), quizId)
                .orElseThrow(() -> new QuizException(ErrorStatus.QUIZ_NOT_SOLVED));

        if (alreadyInReview) {
            throw new QuizException(ErrorStatus.REVIEW_ALREADY_ADDED);
        }

        QuizReviewList reviewItem = QuizReviewList.builder()
                .user(userRepository.findByUserId(Long.valueOf(userId)))
                .quiz(quiz)
                .build();
        quizReviewListRepository.save(reviewItem);
    }

    @Override
    public void removeQuizFromReview(String userId, Long quizId) {
        // 1. 유효한 퀴즈 ID인지 확인
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizException(ErrorStatus.QUIZ_NOT_FOUND));

        // 2. 유효한 사용자 ID인지 확인
        User user = userRepository.findByUserId(Long.valueOf(userId));

        // 3. 복습 리스트에서 해당 퀴즈 조회
        QuizReviewList quizReviewList = quizReviewListRepository.findByUserAndQuiz(user, quiz)
                .orElseThrow(() -> new QuizException(ErrorStatus.QUIZ_REVIEW_NOT_FOUND));

        // 4. 복습 리스트에서 퀴즈 삭제
        quizReviewListRepository.delete(quizReviewList);
    }

    @Override
    public List<QuizReviewResponse> getReviewList(String userId) {
        List<QuizReviewList> reviewList = quizReviewListRepository.findByUser_UserId(Long.valueOf(userId));

        if (reviewList.isEmpty()) {
            throw new QuizException(ErrorStatus.QUIZ_SEARCH_NO_RESULTS);
        }

        // 사용자와 연결된 모든 UserQuiz를 조회
        List<UserQuiz> userQuizzes = userQuizRepository.findByUser_UserId(Long.valueOf(userId));
        Map<Long, Boolean> quizCorrectMap = userQuizzes.stream()
                .collect(Collectors.toMap(userQuiz -> userQuiz.getQuiz().getQuizId(), UserQuiz::isCorrect));

        return reviewList.stream()
                .map(review -> {
                    Quiz quiz = review.getQuiz();
                    boolean isCorrect = quizCorrectMap.getOrDefault(quiz.getQuizId(), false); // 정답 여부 가져오기
                    return QuizConverter.toQuizReviewListResponse(
                            quiz,
                            isCorrect,
                            true, // 복습 리스트 조회이므로 항상 풀린 상태
                            true  // 복습 리스트에 있으므로 항상 true
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizSearchResponse> searchQuizzes(String userId, String keyword) {
        // 1. 퀴즈 검색
        List<Quiz> quizzes = quizRepository.findByQuestionContainingOrDescriptionContaining(keyword, keyword);

        if (quizzes.isEmpty()) {
            throw new QuizException(ErrorStatus.QUIZ_SEARCH_NO_RESULTS);
        }

        // 2. 사용자와 관련된 데이터를 조회
        List<UserQuiz> userQuizzes = userQuizRepository.findByUser_UserId(Long.valueOf(userId));
        Map<Long, UserQuiz> userQuizMap = userQuizzes.stream()
                .collect(Collectors.toMap(userQuiz -> userQuiz.getQuiz().getQuizId(), userQuiz -> userQuiz));

        List<QuizReviewList> reviewList = quizReviewListRepository.findByUser_UserId(Long.valueOf(userId));
        Map<Long, QuizReviewList> reviewQuizMap = reviewList.stream()
                .collect(Collectors.toMap(quizReview -> quizReview.getQuiz().getQuizId(), quizReview -> quizReview));

        // 3. 검색된 퀴즈를 조건에 따라 변환
        return quizzes.stream()
                .map(quiz -> {
                    Long quizId = quiz.getQuizId();

                    // 사용자가 푼 퀴즈인지 확인
                    boolean isSolved = userQuizMap.containsKey(quizId);

                    // 복습 리스트 포함 여부 확인
                    boolean isInReviewList = reviewQuizMap.containsKey(quizId);

                    // 정답 여부 확인
                    boolean isCorrect = userQuizMap.containsKey(quizId) && userQuizMap.get(quizId).isCorrect();

                    // 잠금 여부 확인 (잠금 로직 수정 가능)
                    boolean isLocked = !isSolved && !isCorrect;

                    return QuizConverter.toQuizSearchResponse(quiz, isLocked, isSolved, isInReviewList, isCorrect);
                })
                .collect(Collectors.toList());
    }

    @Override
    public QuizMainPageResponse getQuizMainPageData(String userId) {
        Long uid = Long.valueOf(userId);

        int yesterdayCount = userQuizRepository.countByUserIdAndSolvedDate(uid, LocalDate.now().minusDays(1));
        long totalQuizCount = quizRepository.count();
        long userSolvedCount = userQuizRepository.countDistinctByUserId(uid);

        double progressRate = (double) userSolvedCount / totalQuizCount * 100;

        // 최근 모의고사 점수
        MockTest recentExam = mockTestRepository.findTopByUser_UserIdOrderByCreatedDateDesc(uid);
        Integer recentScore = recentExam != null ? recentExam.getCorrectCount() * 10 : null;

        // 상위 % 계산
        Double topPercentile = recentExam != null ? recentExam.getTopPercentile() : null;
        // 어제 많이 틀린 Top5 퀴즈
        List<Quiz> top5Wrong = userQuizRepository.findTop5MostWrongOnDate(LocalDate.now().minusDays(1));
        List<QuizListResponse> top5Responses = top5Wrong.stream()
                .map(quiz -> QuizConverter.toQuizListResponse(quiz, false, false, false, false))
                .toList();

        return QuizMainPageResponse.builder()
                .yesterdaySolvedCount(yesterdayCount)
                .progressRate(progressRate)
                .latestMockExamScore(recentScore)
                .topPercentile(topPercentile)
                .top5WrongQuizzes(top5Responses)
                .build();
    }

    @Override
    public List<YesterdayQuizResponse> getYesterdaySolvedQuizzes(String userId) {
        Long uid = Long.valueOf(userId);
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<UserQuiz> solvedQuizzes = userQuizRepository.findByUser_UserIdAndSolvedDate(uid, yesterday);

        if (solvedQuizzes.isEmpty()) {
            throw new QuizException(ErrorStatus.QUIZ_SEARCH_NO_RESULTS);
        }

        return solvedQuizzes.stream()
                .map(userQuiz -> {
                    Quiz quiz = userQuiz.getQuiz();
                    boolean isInReviewList = quizReviewListRepository.existsByUser_UserIdAndQuiz_QuizId(uid, quiz.getQuizId());
                    boolean retriedToday = Boolean.TRUE.equals(userQuiz.getRetriedToday());

                    return QuizConverter.toYesterdayQuizResponse(quiz, isInReviewList, retriedToday);
                })
                .collect(Collectors.toList());
    }
}