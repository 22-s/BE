package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizReviewResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizRequest.SubmitQuizRequest;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizListResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizResultResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizDetailResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizSearchResponse;

import java.util.List;

public interface QuizService {
    List<QuizListResponse> getQuizList(String userId, int category);
    QuizDetailResponse getQuizDetail(String userId, Long quizId);
    QuizResultResponse submitQuizAnswer(String userId, Long quizId, SubmitQuizRequest request);
    void addQuizToReview(String userId, Long quizId);
    void removeQuizFromReview(String userId, Long quizId);
    List<QuizSearchResponse> searchQuizzes(String userId, String keyword);
    List<QuizReviewResponse> getReviewList(String userId);
}
