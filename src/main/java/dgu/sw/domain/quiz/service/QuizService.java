package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.dto.QuizDTO.QuizRequest.SubmitQuizRequest;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizListResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizResultResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizDetailResponse;

import java.util.List;

public interface QuizService {
    List<QuizListResponse> getQuizList(String userId, String category);
    QuizDetailResponse getQuizDetail(String userId, Long quizId);
    QuizResultResponse submitQuizAnswer(String userId, Long quizId, SubmitQuizRequest request);
    void addQuizToReview(String userId, Long quizId);
    List<QuizListResponse> searchQuizzes(String keyword);
    List<QuizDetailResponse> getReviewList(String userId);
}
