package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.converter.QuizConverter;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizReviewResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizListResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizDetailResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizResultResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizRequest.SubmitQuizRequest;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.entity.QuizReviewList;
import dgu.sw.domain.quiz.entity.UserQuiz;
import dgu.sw.domain.quiz.repository.QuizRepository;
import dgu.sw.domain.quiz.repository.QuizReviewListRepository;
import dgu.sw.domain.quiz.repository.UserQuizRepository;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.exception.QuizException;
import dgu.sw.global.exception.UserException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public List<QuizListResponse> getQuizList(String userId, String category) {
        List<Quiz> quizzes = quizRepository.findByCategory(category);

        if (quizzes.isEmpty()) {
            throw new QuizException(ErrorStatus.QUIZ_SEARCH_NO_RESULTS);
        }

        List<UserQuiz> userQuizzes = userQuizRepository.findByUser_UserId(Long.valueOf(userId));
        Map<Long, UserQuiz> userQuizMap = userQuizzes.stream()
                .collect(Collectors.toMap(userQuiz -> userQuiz.getQuiz().getQuizId(), userQuiz -> userQuiz));

        // 사용자의 복습 리스트를 가져와 Map으로 변환
        List<QuizReviewList> reviewList = quizReviewListRepository.findByUser_UserId(Long.valueOf(userId));
        Map<Long, QuizReviewList> reviewQuizMap = reviewList.stream()
                .collect(Collectors.toMap(quizReview -> quizReview.getQuiz().getQuizId(), quizReview -> quizReview));

        Long lastSolvedQuizId = userQuizzes.stream()
                .map(userQuiz -> userQuiz.getQuiz().getQuizId())
                .max(Long::compareTo)
                .orElse(null);

        // 퀴즈 목록 생성
        return quizzes.stream()
                .map(quiz -> {
                    Long quizId = quiz.getQuizId();
                    boolean isLocked = (lastSolvedQuizId == null)
                            ? !quizId.equals(quizzes.get(0).getQuizId())
                            : quizId > lastSolvedQuizId + 1;

                    // 사용자가 푼 퀴즈인지 확인
                    boolean isSolved = userQuizMap.containsKey(quizId);

                    // 복습 리스트 포함 여부 확인
                    boolean isInReviewList = reviewQuizMap.containsKey(quizId);

                    // 이미 풀었는지에 따라 처리
                    UserQuiz userQuiz = userQuizMap.get(quizId);
                    if (userQuiz != null) {
                        return QuizConverter.toQuizListResponse(userQuiz, false, isSolved, isInReviewList);
                    } else {
                        return QuizConverter.toQuizListResponse(quiz, isLocked, isInReviewList);
                    }
                })
                .collect(Collectors.toList());
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

        boolean isCorrect = quiz.getAnswer().equals(request.getSelectedAnswer());

        UserQuiz existingUserQuiz = userQuizRepository.findByUser_UserIdAndQuiz_QuizId(Long.valueOf(userId), quizId)
                .orElse(null);

        if (existingUserQuiz != null) {
            existingUserQuiz.updateCorrect(isCorrect);
            userQuizRepository.save(existingUserQuiz);
        } else {
            UserQuiz userQuiz = QuizConverter.toUserQuiz(
                    userRepository.findByUserId(Long.valueOf(userId)),
                    quiz,
                    isCorrect
            );
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
    public List<QuizListResponse> searchQuizzes(String userId, String keyword) {
        List<Quiz> quizzes = quizRepository.findByQuestionContainingOrDescriptionContaining(keyword, keyword);

        if (quizzes.isEmpty()) {
            throw new QuizException(ErrorStatus.QUIZ_SEARCH_NO_RESULTS);
        }

        return quizzes.stream()
                .filter(quiz -> {
                    UserQuiz userQuiz = userQuizRepository.findByUser_UserIdAndQuiz_QuizId(
                            Long.valueOf(userId), quiz.getQuizId()).orElse(null);
                    return userQuiz == null || !userQuiz.isLocked();
                })
                .map(quiz -> QuizConverter.toQuizListResponse(quiz, false, false))
                .collect(Collectors.toList());
    }
}