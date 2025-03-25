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
     * - OAuth 인가 코드로 사용자 정보 조회
     * - 기존 회원이면 로그인, 아니면 회원가입 후 JWT 발급
     */
    @Override
    public AuthUserResponse kakaoLoginWithAccessToken(String accessToken) {
        // 1. 카카오 사용자 정보 요청
        AuthUserProfile userProfile = oAuthUtil.requestUserProfile(OAuthProvider.KAKAO, accessToken);

        // 2. DB에서 기존 사용자 조회 또는 신규 회원가입
        Optional<User> existingUser = userRepository.findByEmail(userProfile.getEmail());
        boolean isNewUser = existingUser.isEmpty();

        User user = existingUser.orElseGet(() -> registerNewUser(userProfile));

        // 3. JWT 발급
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(user);
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        // Redis에 RefreshToken 저장
        redisUtil.saveRefreshToken(user.getUserId().toString(), jwtRefreshToken);

        // 4. 응답 DTO 변환 후 반환
        return AuthConverter.toAuthUserResponse(user, jwtAccessToken, jwtRefreshToken, isNewUser);
    }


    /**
     * 카카오 신규 회원가입
     */
    private User registerNewUser(AuthUserProfile profile) {
        User newUser = AuthConverter.toUser(profile);
        return userRepository.save(newUser);
    }
}