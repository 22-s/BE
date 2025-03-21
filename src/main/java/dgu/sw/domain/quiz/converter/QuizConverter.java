package dgu.sw.domain.quiz.converter;

import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizSearchResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizListResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizDetailResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizResultResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizReviewResponse;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.entity.UserQuiz;
import dgu.sw.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;

public class QuizConverter {

    public static UserQuiz toUserQuiz(User user, Quiz quiz, boolean isCorrect) {
        return UserQuiz.builder()
                .user(user)
                .quiz(quiz)
                .isCorrect(isCorrect)
                .isLocked(false)
                .isReviewed(false)
                .solvedDate(LocalDate.now())
                .build();
    }

    // UserQuiz를 QuizListResponse로 변환
    public static QuizListResponse toQuizListResponse(UserQuiz userQuiz, boolean isLocked, boolean isSolved, boolean isInReviewList) {
        Quiz quiz = userQuiz.getQuiz();
        return QuizListResponse.builder()
                .quizId(quiz.getQuizId())
                .question(quiz.getQuestion())
                .isLocked(isLocked)
                .isSolved(isSolved)
                .isCorrect(userQuiz.isCorrect())
                .isInReviewList(isInReviewList)
                .build();
    }

    // Quiz를 QuizListResponse로 변환 (기본 값)
    public static QuizListResponse toQuizListResponse(Quiz quiz, boolean isLocked, boolean isSolved, boolean isInReviewList, boolean isCorrect) {
        return QuizListResponse.builder()
                .quizId(quiz.getQuizId())
                .question(quiz.getQuestion())
                .isLocked(isLocked)
                .isSolved(isSolved)
                .isInReviewList(isInReviewList)
                .isCorrect(isCorrect)
                .build();
    }

    public static QuizDetailResponse toQuizDetailResponse(Quiz quiz, boolean isSolved, boolean isInReviewList) {
        return QuizDetailResponse.builder()
                .quizId(quiz.getQuizId())
                .question(quiz.getQuestion())
                .questionDetail(quiz.getQuestionDetail())
                .answer(quiz.getAnswer())
                .description(quiz.getDescription())
                .isSolved(isSolved)
                .isInReviewList(isInReviewList)
                .build();
    }

    public static QuizResultResponse toQuizResultResponse(boolean isCorrect) {
        String feedback = isCorrect ? "정답입니다!" : "틀렸습니다. 다시 시도하세요.";
        return QuizResultResponse.builder()
                .isCorrect(isCorrect)
                .feedback(feedback)
                .build();
    }

    public static QuizReviewResponse toQuizReviewListResponse(Quiz quiz, boolean isCorrect, boolean isSolved, boolean isInReviewList) {
        return QuizReviewResponse.builder()
                .quizId(quiz.getQuizId())
                .question(quiz.getQuestion())
                .questionDetail(quiz.getQuestionDetail())
                .answer(quiz.getAnswer())
                .description(quiz.getDescription())
                .isSolved(isSolved)
                .isInReviewList(isInReviewList)
                .isCorrect(isCorrect)
                .build();
    }

    public static QuizSearchResponse toQuizSearchResponse(Quiz quiz, boolean isLocked, boolean isSolved, boolean isInReviewList, boolean isCorrect) {
        return QuizSearchResponse.builder()
                .quizId(quiz.getQuizId())
                .question(quiz.getQuestion())
                .isLocked(isLocked)
                .isSolved(isSolved)
                .isInReviewList(isInReviewList)
                .isCorrect(isCorrect)
                .build();
    }

}