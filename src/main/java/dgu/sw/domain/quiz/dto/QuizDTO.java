package dgu.sw.domain.quiz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class QuizDTO {

    public static class QuizRequest {

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SubmitQuizRequest {
            @NotNull
            private Long userId;
            @NotNull
            private String selectedAnswer;
        }
    }

    public static class QuizResponse {

        @Getter
        @Builder
        public static class QuizListResponse {
            private Long quizId;
            private String question;
            private boolean isLocked;
            private boolean isCorrect;
        }

        @Getter
        @Builder
        public static class QuizDetailResponse {
            private Long quizId;
            private String question;
            private String answer;
            private String description;
            private boolean isCorrect;
        }

        @Getter
        @Builder
        public static class QuizResultResponse {
            private boolean isCorrect;
            private String feedback;
        }

        @Getter
        @Builder
        public static class QuizReviewResponse {
            private Long quizId;
            private String question;
            private String feedback;
        }
    }
}