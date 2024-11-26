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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final UserQuizRepository userQuizRepository;
    private final QuizReviewListRepository quizReviewListRepository;

    @Override
    public List<QuizListResponse> getQuizList(String userId, String category) {
        // 1. 카테고리로 퀴즈 리스트 조회
        List<Quiz> quizzes = quizRepository.findByCategory(category);

        // 2. 사용자 ID로 사용자가 푼 UserQuiz 데이터 조회
        List<UserQuiz> userQuizzes = userQuizRepository.findByUser_UserId(Long.valueOf(userId));

        // 3. UserQuiz 정보를 Map으로 변환 (key: quizId, value: UserQuiz)
        Map<Long, UserQuiz> userQuizMap = userQuizzes.stream()
                .collect(Collectors.toMap(userQuiz -> userQuiz.getQuiz().getQuizId(), userQuiz -> userQuiz));

        // 4. 카테고리별 첫 번째 퀴즈를 식별
        Long firstQuizId = quizzes.stream()
                .map(Quiz::getQuizId)
                .sorted()
                .findFirst()
                .orElse(null); // 퀴즈가 없으면 null

        // 5. Quiz 데이터를 순회하며 QuizListResponse 생성
        return quizzes.stream()
                .map(quiz -> {
                    Long quizId = quiz.getQuizId();
                    UserQuiz userQuiz = userQuizMap.get(quizId);

                    // 사용자가 아무것도 풀지 않은 경우 첫 번째 퀴즈만 잠금 해제
                    boolean isLocked = userQuizzes.isEmpty() && !quizId.equals(firstQuizId);

                    // UserQuiz가 존재하면 정답 여부를 포함하여 응답 생성
                    if (userQuiz != null) {
                        return QuizConverter.toQuizListResponse(userQuiz, isLocked, false);
                    } else {
                        // UserQuiz가 없으면 기본 값으로 생성
                        return QuizConverter.toQuizListResponse(quiz, isLocked, false);
                    }
                })
                .collect(Collectors.toList());
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