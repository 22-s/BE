package dgu.sw.domain.quiz.converter;

import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.CreateMockTestResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.MockTestQuestionResponse;
import dgu.sw.domain.quiz.entity.MockTest;
import dgu.sw.domain.quiz.entity.MockTestQuiz;

import java.util.List;
import java.util.stream.Collectors;

public class MockTestConverter {
    public static CreateMockTestResponse toCreateMockTestResponse(MockTest mockTest, List<MockTestQuiz> mockTestQuizzes) {
        List<MockTestQuestionResponse> quizResponses = mockTestQuizzes.stream()
                .map(mtq -> new MockTestQuestionResponse(mtq.getQuiz().getQuizId(), mtq.getQuiz().getQuestion()))
                .collect(Collectors.toList());

        return CreateMockTestResponse.builder()
                .mockTestId(mockTest.getMockTestId())
                .createdDate(mockTest.getCreatedDate())
                .isCompleted(mockTest.isCompleted())
                .correctCount(mockTest.getCorrectCount())
                .quizzes(quizResponses)
                .build();
    }
}
