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
            private String questionDetail;
        }

        @Getter
        @Builder
        public static class SubmitMockTestResponse {
            private Long mockTestId;
            private int correctCount;
            private double topPercentile;
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

        @Getter
        @Builder
        public static class MockTestResultResponse {
            private Long mockTestId;
            private int attemptCount; // 몇 번째 모의고사인지
            private int score; // 100점 만점 기준
            private int scoreChange; // 이전 모의고사 대비 상승/하락 점수
            private double topPercentile; // 상위 % 위치
            private double topPercentileChange; // 이전 모의고사 대비 등락률

            private List<CategoryResult> categoryResults; // 카테고리별 정답 수
            private List<MockTestQuestionResult> questionResults; // 전체 문제 목록

            @Getter
            @Builder
            public static class CategoryResult {
                private String category;
                private int correctCount;
                private int totalCount;
            }

            @Getter
            @Builder
            public static class MockTestQuestionResult {
                private Long quizId;
                private String category;
                private String question;
                private boolean isCorrect;
            }
        }
    }
}
