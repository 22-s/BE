package dgu.sw.domain.admin.dto;

import dgu.sw.global.security.OAuthProvider;
import lombok.*;

import java.time.LocalDateTime;

public class AdminDTO {

    public static class AdminRequest {
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
    }
}
