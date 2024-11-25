package dgu.sw.domain.quiz.converter;

import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizListResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizDetailResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizResultResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizReviewResponse;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.entity.UserQuiz;
import dgu.sw.domain.user.entity.User;

import java.util.List;

public class QuizConverter {

    public static UserQuiz toUserQuiz(User user, Quiz quiz, boolean isCorrect) {
        return UserQuiz.builder()
                .user(user)
                .quiz(quiz)
                .isCorrect(isCorrect)
                .isLocked(false)
                .isReviewed(false)
                .build();
    }

    public static QuizListResponse toQuizListResponse(UserQuiz userQuiz) {
        Quiz quiz = userQuiz.getQuiz();
        return QuizListResponse.builder()
                .quizId(quiz.getQuizId())
                .question(quiz.getQuestion())
                .isLocked(userQuiz.isLocked())
                .isCorrect(userQuiz.isCorrect())
                .build();
    }

    public static QuizDetailResponse toQuizDetailResponse(Quiz quiz, UserQuiz userQuiz) {
        return QuizDetailResponse.builder()
                .quizId(quiz.getQuizId())
                .question(quiz.getQuestion())
                .answer(quiz.getAnswer())
                .description(quiz.getDescription())
                .isCorrect(userQuiz.isCorrect())
                .build();
    }

    public static QuizResultResponse toQuizResultResponse(boolean isCorrect) {
        String feedback = isCorrect ? "정답입니다!" : "틀렸습니다. 다시 시도하세요.";
        return QuizResultResponse.builder()
                .isCorrect(isCorrect)
                .feedback(feedback)
                .build();
    }

    public static QuizReviewResponse toQuizReviewResponse(Quiz quiz, String feedback) {
        return QuizReviewResponse.builder()
                .quizId(quiz.getQuizId())
                .question(quiz.getQuestion())
                .feedback(feedback)
                .build();
    }
}