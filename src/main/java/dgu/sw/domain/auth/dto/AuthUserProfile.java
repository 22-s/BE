package dgu.sw.domain.auth.dto;

import dgu.sw.global.security.OAuthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 OAuth 사용자 프로필 정보
 */
@Getter
@Builder
public class AuthUserProfile {

    private OAuthProvider provider;    // KAKAO, GOOGLE, NAVER 구분
    private String email;
    private String nickname;
    private String profileImage;

    /**
     * 카카오 프로필로부터 AuthUserProfile 생성
     */
    public static AuthUserProfile ofKakao(AuthDTO.AuthResponse.KakaoProfile profile) {
        return AuthUserProfile.builder()
                .provider(OAuthProvider.KAKAO)
                .email(profile.getKakao_account().getEmail())
                .nickname(profile.getProperties().getNickname())
                .profileImage(profile.getProperties().getProfile_image())
                .build();
    }
}
