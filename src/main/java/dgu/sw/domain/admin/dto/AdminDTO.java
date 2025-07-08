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
    }
}
