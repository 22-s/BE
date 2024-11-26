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
import dgu.sw.domain.user.repository.UserRepository;
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
        // 1. 카테고리로 퀴즈 리스트 조회
        List<Quiz> quizzes = quizRepository.findByCategory(category);

        // 2. 사용자 ID로 사용자가 푼 UserQuiz 데이터 조회
        List<UserQuiz> userQuizzes = userQuizRepository.findByUser_UserId(Long.valueOf(userId));

        // 3. UserQuiz 정보를 Map으로 변환 (key: quizId, value: UserQuiz)
        Map<Long, UserQuiz> userQuizMap = userQuizzes.stream()
                .collect(Collectors.toMap(userQuiz -> userQuiz.getQuiz().getQuizId(), userQuiz -> userQuiz));

        // 4. 마지막으로 푼 퀴즈의 ID를 식별
        Long lastSolvedQuizId = userQuizzes.stream()
                .map(userQuiz -> userQuiz.getQuiz().getQuizId())
                .max(Long::compareTo)
                .orElse(null); // 사용자가 푼 퀴즈가 없으면 null

        // 5. Quiz 데이터를 순회하며 QuizListResponse 생성
        return quizzes.stream()
                .map(quiz -> {
                    Long quizId = quiz.getQuizId();

                    // 사용자가 푼 문제 + 그 다음 문제까지 잠금 해제
                    boolean isLocked = (lastSolvedQuizId == null)
                            ? !quizId.equals(quizzes.get(0).getQuizId()) // 푼 문제가 없으면 첫 번째 문제만 잠금 해제
                            : quizId > lastSolvedQuizId + 1; // 마지막 푼 문제 다음 문제까지 잠금 해제

                    // UserQuiz가 존재하면 정답 여부를 포함하여 응답 생성
                    UserQuiz userQuiz = userQuizMap.get(quizId);
                    if (userQuiz != null) {
                        return QuizConverter.toQuizListResponse(userQuiz, false, false);
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
        // 1. 퀴즈 데이터 조회
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 퀴즈입니다."));

        // 2. 사용자가 이미 해당 퀴즈를 풀었는지 확인
        boolean alreadySolved = userQuizRepository.existsByUser_UserIdAndQuiz_QuizId(Long.valueOf(userId), quizId);

        if (alreadySolved) {
            throw new IllegalStateException("이미 푼 퀴즈입니다.");
        }

        // 3. 정답 여부 확인
        boolean isCorrect = quiz.getAnswer().equals(request.getSelectedAnswer());

        // 4. UserQuiz 엔티티 생성 및 저장
        UserQuiz userQuiz = QuizConverter.toUserQuiz(
                userRepository.findByUserId(Long.valueOf(userId)), // 유저 엔티티 조회
                quiz,
                isCorrect
        );
        userQuizRepository.save(userQuiz);

        // 5. 결과 반환
        return QuizConverter.toQuizResultResponse(isCorrect);
    }

    @Override
    public void addQuizToReview(String userId, Long quizId) {
        // 복습 리스트에 퀴즈 추가
    }

    public List<QuizListResponse> searchQuizzes(String keyword) {
        return null;
    }
}