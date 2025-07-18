package dgu.sw.domain.admin.dto;

import dgu.sw.domain.feedback.dto.FeedbackCategory;
import dgu.sw.domain.quiz.entity.QuizLevel;
import dgu.sw.global.security.OAuthProvider;
import lombok.*;

import java.time.LocalDateTime;

public class AdminDTO {

    public static class AdminRequest {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AdminLoginRequest {
            private String email;
            private String password;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AdminMannerRequest {
            private String category;
            private String title;
            private String imageUrl;
            private String content;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AdminQuizRequest {
            private String category;
            private String question;
            private String answer;
            private String description;
            private String questionDetail;
            private QuizLevel quizLevel;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AdminVocaRequest {
            private String category;
            private String term;
            private String description;
            private String example;
        }
    }

    public static class AdminResponse {

        @Getter
        @Builder
        @AllArgsConstructor
        public static class AdminLoginResponse {
            private String accessToken;
            private String refreshToken;
            private Long userId;
            private String email;
            private String nickname;
        }

        @Getter
        @Builder
        public static class AdminUserResponse {
            private Long userId;
            private String email;
            private String nickname;
            private String profileImage;
            private OAuthProvider provider;
            private LocalDateTime createdAt;

        }

        @Getter
        @Builder
        public static class AdminMannerResponse {
            private Long mannerId;
            private String category;
            private String title;
            private String imageUrl;
            private String content;
        }

        @Getter
        @Builder
        public static class AdminQuizResponse {
            private Long quizId;
            private String category;
            private String question;
            private String answer;
            private String description;
            private String questionDetail;
            private QuizLevel quizLevel;
        }

        @Getter
        @Builder
        public static class AdminVocaResponse {
            private Long vocaId;
            private String category;
            private String term;
            private String description;
            private String example;
        }

        @Getter
        @Builder
        public static class AdminFeedbackResponse {
            private Long feedbackId;
            private String email;        // 작성자 이메일 (익명 아닐 때만)
            private String nickname;     // 작성자 닉네임 (익명 아닐 때만)
            private FeedbackCategory category;
            private String content;
            private Boolean isAnonymous;
        }
    }
}
