package dgu.sw.global.security;

public class OAuthConstants {

    // Token 요청 URL
    public static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";

    // Profile 요청 URL
    public static final String KAKAO_PROFILE_URL = "https://kapi.kakao.com/v2/user/me";
    public static final String GOOGLE_PROFILE_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    public static final String NAVER_PROFILE_URL = "https://openapi.naver.com/v1/nid/me";

    private OAuthConstants() {
        // 상수 클래스는 인스턴스화 금지
    }
}
