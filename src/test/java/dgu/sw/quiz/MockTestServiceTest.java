package dgu.sw.quiz;

import dgu.sw.domain.quiz.dto.MockTestDTO;
import dgu.sw.domain.quiz.entity.MockTest;
import dgu.sw.domain.quiz.entity.MockTestQuiz;
import dgu.sw.domain.quiz.entity.Quiz;
import dgu.sw.domain.quiz.repository.MockTestQuizRepository;
import dgu.sw.domain.quiz.repository.MockTestRepository;
import dgu.sw.domain.quiz.service.MockTestService;
import dgu.sw.domain.quiz.service.MockTestServiceImpl;
import dgu.sw.domain.user.entity.User;
import dgu.sw.global.security.OAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MockTestServiceTest {
    @InjectMocks
    private MockTestServiceImpl mockTestService;
    @Mock
    private MockTestRepository mockTestRepository;
    @Mock
    private MockTestQuizRepository mockTestQuizRepository;

    private User user;
    private MockTest firstTest;

    @BeforeEach
    void Setup(){
        user = User.builder()
                .userId(1L)
                .nickname("alice")
                .email("alice@example.com")
                .password("secret")
                .joinDate(LocalDate.of(2025, 6, 23))
                .profileImage("profile.png")
                .provider(OAuthProvider.GOOGLE)
                .build();

        firstTest = MockTest.builder()
                .mockTestId(100L)
                .user(user)
                .correctCount(1)
                .topPercentile(80.0)
                .isCompleted(true)
                .build();
    }

    @Test
    void 첫회차일때_score와_scoreChange가_score와_같아야한다() {
        //given
        when(mockTestRepository
                .findByUser_UserIdAndIsCompletedTrueOrderByMockTestIdAsc(1L))
                .thenReturn(Collections.singletonList(firstTest));

        Quiz q1 = Quiz.builder().quizId(10L).category("Math").question("2+2?").build();
        Quiz q2 = Quiz.builder().quizId(11L).category("Math").question("3+3?").build();
        MockTestQuiz mtq1 = MockTestQuiz.builder()
                .quiz(q1).isCorrect(true).build();
        MockTestQuiz mtq2 = MockTestQuiz.builder()
                .quiz(q2).isCorrect(false).build();
        when(mockTestQuizRepository.findByMockTest_MockTestId(100L))
                .thenReturn(List.of(mtq1, mtq2));

        //when
        List<MockTestDTO.MockTestResponse.MockTestResultResponse> results =
                mockTestService.getAllMockTestResults("1");

        //then
        assertEquals(1, results.size(), "결과를 한 건만 반환");
        MockTestDTO.MockTestResponse.MockTestResultResponse response = results.get(0);
        
        
        assertEquals(50, response.getScore(), "첫 회차 점수는 50점");
        assertEquals(response.getScore(), response.getScoreChange(),
                "첫 회차의 scoreChange는 score와 동일");
        assertEquals(response.getTopPercentile(),response.getTopPercentileChange());

        verify(mockTestRepository)
                .findByUser_UserIdAndIsCompletedTrueOrderByMockTestIdAsc(1L);
        verify(mockTestQuizRepository)
                .findByMockTest_MockTestId(100L);
    }
}
