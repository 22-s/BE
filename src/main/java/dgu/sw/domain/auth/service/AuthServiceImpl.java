package dgu.sw.domain.auth.service;

import dgu.sw.domain.auth.converter.AuthConverter;
import dgu.sw.domain.auth.dto.AuthDTO.AuthResponse.AuthUserResponse;
import dgu.sw.domain.auth.dto.AuthUserProfile;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.config.redis.RedisUtil;
import dgu.sw.global.security.JwtTokenProvider;
import dgu.sw.global.security.OAuthProvider;
import dgu.sw.global.security.OAuthUtil;
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

    /**
     * 카카오 로그인 처리
     */
    @Override
    public AuthUserResponse kakaoLoginWithAccessToken(String accessToken) {
        return handleSocialLogin(OAuthProvider.KAKAO, accessToken);
    }

    /**
     * 네이버 로그인 처리
     */
    @Override
    public AuthUserResponse naverLoginWithAccessToken(String accessToken) {
        return handleSocialLogin(OAuthProvider.NAVER, accessToken);
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
        // 1. 소셜 사용자 프로필 조회 (provider 기반)
        AuthUserProfile userProfile = oAuthUtil.requestUserProfile(provider, accessToken);


        // 2. 기존 회원 여부 확인
        Optional<User> existingUser = userRepository.findByEmail(userProfile.getEmail());
        boolean isNewUser = existingUser.isEmpty();

        // 3. 신규 회원이면 회원가입
        User user = existingUser.orElseGet(() -> registerNewUser(userProfile));

        // 4. JWT Access & Refresh Token 발급
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(user);
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        // 5. Redis에 RefreshToken 저장
        redisUtil.saveRefreshToken(user.getUserId().toString(), jwtRefreshToken);

        // 6. 응답 객체로 변환 후 반환
        return AuthConverter.toAuthUserResponse(user, jwtAccessToken, jwtRefreshToken, isNewUser);
    }

    /**
     * 신규 회원 DB 등록
     */
    private User registerNewUser(AuthUserProfile profile) {
        User newUser = AuthConverter.toUser(profile);
        System.out.println(newUser.getProvider());
        return userRepository.save(newUser);
    }
}