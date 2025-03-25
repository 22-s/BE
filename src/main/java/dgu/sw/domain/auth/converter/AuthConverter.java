package dgu.sw.domain.auth.converter;

import dgu.sw.domain.auth.dto.AuthDTO.AuthResponse.AuthUserResponse;
import dgu.sw.domain.auth.dto.AuthUserProfile;
import dgu.sw.domain.user.entity.User;
import dgu.sw.global.security.OAuthProvider;

public class AuthConverter {

    public static User toUser(AuthUserProfile profile) {
        return User.builder()
                .email(profile.getEmail())
                .nickname(profile.getNickname())
                .profileImage(profile.getProfileImage())
                .provider(OAuthProvider.KAKAO) // 카카오 로그인
                .build();
    }

    public static AuthUserResponse toAuthUserResponse(User user, String accessToken, String refreshToken, boolean isNew) {
        return AuthUserResponse.builder()
                .provider(user.getProvider().name())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNew(isNew)
                .build();
    }
}
