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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final UserQuizRepository userQuizRepository;
    private final QuizReviewListRepository quizReviewListRepository;

    @Override
    public List<QuizListResponse> getQuizList(String userId, String category) {
        // UserQuiz와 Quiz 데이터를 카테고리로 필터링
        return null;
    }

    @Override
    public QuizDetailResponse getQuizDetail(String userId, Long quizId) {
        // UserQuiz와 Quiz 데이터를 조회
        return null;
    }

    @Override
    public QuizResultResponse submitQuizAnswer(String userId, Long quizId, SubmitQuizRequest request) {
        return null;
    }

    @Override
    public void addQuizToReview(String userId, Long quizId) {
        // 복습 리스트에 퀴즈 추가
    }

    public List<QuizListResponse> searchQuizzes(String keyword) {
        return null;
    }
}