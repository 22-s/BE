package dgu.sw.domain.quiz.service;

import dgu.sw.domain.quiz.converter.MockTestConverter;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.CreateMockTestResponse;
import dgu.sw.domain.quiz.entity.MockTest;
import dgu.sw.domain.quiz.entity.MockTestQuiz;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.repository.MockTestQuizRepository;
import dgu.sw.domain.quiz.repository.MockTestRepository;
import dgu.sw.domain.quiz.repository.QuizRepository;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MockTestServiceImpl implements MockTestService {
    private final MockTestRepository mockTestRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final MockTestQuizRepository mockTestQuizRepository;

    @Override
    @Transactional
    public CreateMockTestResponse startMockTest(String userId) {
        User user = userRepository.findByUserId(Long.valueOf(userId));

        // 랜덤한 10문제 가져오기
        List<Quiz> randomQuizzes = quizRepository.findRandomQuizzes(10);

        // 모의고사 생성
        MockTest mockTest = MockTest.builder()
                .user(user)
                .createdDate(LocalDate.now())
                .isCompleted(false)
                .correctCount(0)
                .build();
        mockTestRepository.save(mockTest);

        // 모의고사 문제 저장
        List<MockTestQuiz> mockTestQuizzes = randomQuizzes.stream()
                .map(quiz -> MockTestQuiz.builder()
                        .mockTest(mockTest)
                        .quiz(quiz)
                        .isCorrect(false) // 초기값 설정
                        .build())
                .collect(Collectors.toList());

        mockTestQuizRepository.saveAll(mockTestQuizzes);

        return MockTestConverter.toCreateMockTestResponse(mockTest, mockTestQuizzes);
    }
}
