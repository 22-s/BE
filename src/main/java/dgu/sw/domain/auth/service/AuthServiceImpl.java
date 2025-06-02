package dgu.sw.domain.auth.service;

import dgu.sw.domain.auth.converter.AuthConverter;
import dgu.sw.domain.auth.dto.AuthDTO.AuthResponse.AuthUserResponse;
import dgu.sw.domain.auth.dto.AuthUserProfile;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.config.redis.RedisUtil;
import dgu.sw.global.exception.OAuthException;
import dgu.sw.global.security.JwtTokenProvider;
import dgu.sw.global.security.OAuthProvider;
import dgu.sw.global.security.OAuthUtil;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final OAuthUtil oAuthUtil;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    // 카카오 로그인 처리
    @Override
    public AuthUserResponse kakaoLoginWithAccessToken(String accessToken) {
        return handleSocialLogin(OAuthProvider.KAKAO, accessToken);
    }

    // 네이버 로그인 처리
    @Override
    public AuthUserResponse naverLoginWithAccessToken(String accessToken) {
        return handleSocialLogin(OAuthProvider.NAVER, accessToken);
    }

    // 구글 로그인 처리
    @Override
    public AuthUserResponse googleLoginWithAccessToken(String accessToken) {
        return handleSocialLogin(OAuthProvider.GOOGLE, accessToken);
    }

    // 애플 로그인 처리
    @Override
    public AuthUserResponse appleLoginWithAccessToken(String identityToken) {
        return handleSocialLogin(OAuthProvider.APPLE, identityToken);
    }


    /**
     * 공통 소셜 로그인 처리
     * - 소셜 access token으로 사용자 정보 조회
     * - DB에 기존 회원 존재 여부 확인
     * - 신규 회원이면 회원가입
     * - JWT 발급 + RefreshToken 저장
     * - 응답 DTO로 변환하여 반환
     */
    private AuthUserResponse handleSocialLogin(OAuthProvider provider, String accessToken) {
        // 1. accessToken 유효성 검사
        if (accessToken == null || accessToken.isBlank()) {
            throw new OAuthException(ErrorStatus.OAUTH_ACCESS_TOKEN_MISSING);
        }

        // 2. 소셜 사용자 프로필 조회
        AuthUserProfile userProfile;
        try {
            userProfile = oAuthUtil.requestUserProfile(provider, accessToken);
        } catch (Exception e) {
            throw new OAuthException(ErrorStatus.OAUTH_USERINFO_FETCH_FAILED);
        }

        // 3. 이메일 유무 체크
        if (userProfile.getEmail() == null || userProfile.getEmail().isBlank()) {
            throw new OAuthException(ErrorStatus.OAUTH_EMAIL_NOT_PROVIDED);
        }

        // 4. 이메일로 기존 회원 조회 && 이미 다른 provider로 가입된 경우 에러
        Optional<User> existingUserOpt = userRepository.findByEmail(userProfile.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // 이미 다른 provider로 가입된 경우
            if (!existingUser.getProvider().equals(provider)) {
                throw new OAuthException(ErrorStatus.OAUTH_PROVIDER_CONFLICT);
            }
        }


        // 5. 신규 회원이면 회원가입 또는 기존 유저 반환
        User user = existingUserOpt.orElseGet(() -> registerNewUser(userProfile));

        // 6. JWT 발급
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(user);
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        // 7. Redis에 RefreshToken 저장
        try {
            redisUtil.saveRefreshToken(user.getUserId().toString(), jwtRefreshToken);
        } catch (Exception e) {
            throw new OAuthException(ErrorStatus.OAUTH_REFRESH_TOKEN_SAVE_FAILED);
        }

        boolean isNewUser = existingUserOpt.isEmpty();
        return AuthConverter.toAuthUserResponse(user, jwtAccessToken, jwtRefreshToken, isNewUser);
    }

    /**
     * 신규 회원 DB 등록
     */
    private User registerNewUser(AuthUserProfile profile) {
        User newUser = AuthConverter.toUser(profile);
        return userRepository.save(newUser);
    }
}