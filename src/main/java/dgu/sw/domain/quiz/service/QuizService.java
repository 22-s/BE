package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.YesterdayQuizResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizMainPageResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizReviewResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizRequest.SubmitQuizRequest;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizListResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizResultResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizDetailResponse;
import dgu.sw.domain.quiz.dto.QuizDTO.QuizResponse.QuizSearchResponse;

import java.util.List;

public interface QuizService {
    List<QuizListResponse> getQuizList(Long userId, int category);
    QuizDetailResponse getQuizDetail(Long userId, Long quizId);
    QuizResultResponse submitQuizAnswer(Long userId, Long quizId, SubmitQuizRequest request);
    void addQuizToReview(Long userId, Long quizId);
    void removeQuizFromReview(Long userId, Long quizId);
    List<QuizSearchResponse> searchQuizzes(Long userId, String keyword);
    List<QuizReviewResponse> getReviewList(Long userId);
    QuizMainPageResponse getQuizMainPageData(Long userId);
    List<YesterdayQuizResponse> getYesterdaySolvedQuizzes(Long userId);
}
