package dgu.sw.domain.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthDTO {
    public static class AuthRequest {

        @Getter
        public static class AuthInfoRequest{
            private String email;
        }

        @Getter
        @NoArgsConstructor
        public static class SocialLoginRequest {
            private String accessToken;
        }
    }

    public static class AuthResponse {

        // 공통 OAuth 토큰 응답 (카카오, 구글, 네이버 공통 구조)
        @Getter
        public static class OAuthToken {
            private String access_token;
            private String token_type;
            private String refresh_token;
            private int expires_in;
            private String scope;                      // 네이버/카카오는 있음
            private int refresh_token_expires_in;     // 카카오 전용
        }

        // 카카오 프로필 응답 (카카오 전용)
        @Getter
        public static class KakaoProfile {
            private Long id;
            private String connected_at;
            private Properties properties;
            private KakaoAccount kakao_account;

            @Getter
            public static class Properties {
                private String nickname;
                private String profile_image;
                private String thumbnail_image;
            }

            @Getter
            public static class KakaoAccount {
                private Boolean profile_nickname_needs_agreement;
                private Boolean profile_image_needs_agreement;
                private Profile profile;
                private Boolean has_email;
                private Boolean email_needs_agreement;
                private Boolean is_email_valid;
                private Boolean is_email_verified;
                private String email;

                @Getter
                public static class Profile {
                    private String nickname;
                    private String thumbnail_image_url;
                    private String profile_image_url;
                    private Boolean is_default_image;
                    private Boolean is_default_nickname;
                }
            }
        }

        // 프론트로 리턴하는 공통 유저 정보 응답 (Provider 구분 없이)
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class AuthUserResponse {
            private String provider;        // KAKAO, GOOGLE, NAVER
            private String email;
            private String nickname;
            private String profileImage;
            private String accessToken;
            private String refreshToken;
            private boolean isNew; // 신규 가입 여부
        }
    }
}
