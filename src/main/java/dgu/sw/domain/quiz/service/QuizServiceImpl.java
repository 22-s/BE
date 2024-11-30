package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.converter.QuizConverter;
import dgu.sw.domain.quiz.dto.QuizDTO;
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

        Long lastSolvedQuizId = userQuizzes.stream()
                .map(userQuiz -> userQuiz.getQuiz().getQuizId())
                .max(Long::compareTo)
                .orElse(null);

        return quizzes.stream()
                .map(quiz -> {
                    Long quizId = quiz.getQuizId();
                    boolean isLocked = (lastSolvedQuizId == null)
                            ? !quizId.equals(quizzes.get(0).getQuizId())
                            : quizId > lastSolvedQuizId + 1;

                    // 사용자가 푼 퀴즈인지 확인
                    boolean isSolved = userQuizMap.containsKey(quizId);

                    UserQuiz userQuiz = userQuizMap.get(quizId);
                    if (userQuiz != null) {
                        return QuizConverter.toQuizListResponse(userQuiz, false, true, false);
                    } else {
                        return QuizConverter.toQuizListResponse(quiz, isLocked, false);
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

        return QuizConverter.toQuizDetailResponse(quiz, isSolved);
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
    public List<QuizDetailResponse> getReviewList(String userId) {
        List<QuizReviewList> reviewList = quizReviewListRepository.findByUser_UserId(Long.valueOf(userId));

        if (reviewList.isEmpty()) {
            throw new QuizException(ErrorStatus.QUIZ_SEARCH_NO_RESULTS);
        }

        return reviewList.stream()
                .map(review -> QuizConverter.toQuizDetailResponse(review.getQuiz(), true))
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