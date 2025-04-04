package dgu.sw.domain.quiz.converter;

import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.MockTestResultResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestRequest.SubmitMockTestRequest;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.SubmittedQuizResult;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.SubmitMockTestResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.CreateMockTestResponse;
import dgu.sw.domain.quiz.dto.MockTestDTO.MockTestResponse.MockTestQuestionResponse;
import dgu.sw.domain.quiz.entity.MockTest;
import dgu.sw.domain.quiz.entity.MockTestQuiz;

import java.util.List;
import java.util.Map;
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

    public static SubmitMockTestResponse toSubmitMockTestResponse(MockTest mockTest, List<MockTestQuiz> mockTestQuizzes, List<SubmitMockTestRequest.Answer> answers) {
        // quizId → selectedAnswer 매핑
        Map<Long, String> selectedAnswerMap = answers.stream()
                .collect(Collectors.toMap(SubmitMockTestRequest.Answer::getQuizId, SubmitMockTestRequest.Answer::getSelectedAnswer));

        List<SubmittedQuizResult> results = mockTestQuizzes.stream()
                .map(mtq -> SubmittedQuizResult.builder()
                        .quizId(mtq.getQuiz().getQuizId())
                        .question(mtq.getQuiz().getQuestion())
                        .selectedAnswer(selectedAnswerMap.get(mtq.getQuiz().getQuizId())) // 여기!
                        .isCorrect(mtq.isCorrect())
                        .build())
                .collect(Collectors.toList());

        return SubmitMockTestResponse.builder()
                .mockTestId(mockTest.getMockTestId())
                .correctCount(mockTest.getCorrectCount())
                .topPercentile(mockTest.getTopPercentile())
                .results(results)
                .build();
    }

    public static MockTestResultResponse toMockTestResultResponse(
            MockTest mockTest,
            int attemptCount,
            int score,
            int scoreChange,
            double topPercentileChange,
            List<MockTestResultResponse.CategoryResult> categoryResults,
            List<MockTestResultResponse.MockTestQuestionResult> questionResults
    ) {
        return MockTestResultResponse.builder()
                .mockTestId(mockTest.getMockTestId())
                .attemptCount(attemptCount)
                .score(score)
                .scoreChange(scoreChange)
                .topPercentile(mockTest.getTopPercentile())
                .topPercentileChange(topPercentileChange)
                .categoryResults(categoryResults)
                .questionResults(questionResults)
                .build();
    }
}
