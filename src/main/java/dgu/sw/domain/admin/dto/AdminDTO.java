package dgu.sw.domain.admin.dto;

import dgu.sw.global.security.OAuthProvider;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AdminDTO {

    public static class AdminRequest {

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
    }
}
