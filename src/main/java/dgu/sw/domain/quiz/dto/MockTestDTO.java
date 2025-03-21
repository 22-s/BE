package dgu.sw.domain.quiz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class MockTestDTO {
    public static class MockTestRequest {
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SubmitMockTestRequest {
            @NotNull
            private Long mockTestId;

            @NotNull
            private List<Answer> answers;

            @Getter
            @AllArgsConstructor
            @NoArgsConstructor
            @Builder
            public static class Answer {
                @NotNull
                private Long quizId;
                @NotNull
                private String selectedAnswer;
            }
        }
    }

    public static class MockTestResponse {

        @Getter
        @Builder
        public static class CreateMockTestResponse {
            private Long mockTestId;
            private LocalDate createdDate;
            private boolean isCompleted;
            private int correctCount;
            private List<MockTestQuestionResponse> quizzes;
        }

        @Getter
        @Builder
        @AllArgsConstructor
        public static class MockTestQuestionResponse {
            private Long quizId;
            private String question;
        }

        @Getter
        @Builder
        public static class SubmitMockTestResponse {
            private Long mockTestId;
            private int correctCount;
            private List<SubmittedQuizResult> results;
        }

        @Getter
        @Builder
        public static class SubmittedQuizResult {
            private Long quizId;
            private String question;
            private String selectedAnswer;
            private boolean isCorrect;
        }
    }
}
