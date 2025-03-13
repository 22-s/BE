package dgu.sw.domain.user.service;

import dgu.sw.domain.user.converter.UserConverter;
import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignInResponse;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.security.JwtTokenProvider;
import dgu.sw.global.security.JwtUtil;
import dgu.sw.global.config.redis.RedisUtil;
import dgu.sw.global.exception.UserException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignUpResponse;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignUpRequest;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignInRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        // 비밀번호 암호화 후 저장
        String encryptedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        User user = UserConverter.toUser(signUpRequest, encryptedPassword);
        userRepository.save(user);

        return UserConverter.toSignUpResponseDTO(user);
    }

    /**
     * 로그인
     */
    @Override
    public SignInResponse signIn(HttpServletResponse response, SignInRequest signInRequest) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new UserException(ErrorStatus.INVALID_CREDENTIALS);
        }

        // AccessToken & RefreshToken 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // Redis에 RefreshToken 저장
        redisUtil.saveRefreshToken(user.getUserId().toString(), refreshToken);

        return UserConverter.toSignInResponseDTO(accessToken, refreshToken);
    }

    /**
     * 로그아웃
     */
    @Override
    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        // 요청에서 AccessToken 추출
        String accessToken = resolveToken(request);
        if (accessToken == null) {
            throw new UserException(ErrorStatus.TOKEN_NOT_FOUND);
        }

        // AccessToken 만료 시간 가져오기
        Long expiration = jwtUtil.getExpiration(accessToken);

        // Redis에 AccessToken을 블랙리스트로 등록 (로그아웃 처리)
        redisUtil.addTokenToBlacklist(accessToken, expiration);

        // Redis에서 RefreshToken 삭제
        String userId = String.valueOf(jwtUtil.extractUserId(accessToken));
        redisUtil.deleteRefreshToken(userId);
    }

    /**
     * AccessToken 갱신
     */
    @Override
    public SignInResponse refreshAccessToken(String refreshToken, HttpServletResponse response) {
        Long extractedUserId = jwtUtil.extractUserId(refreshToken);
        if (extractedUserId == null) {
            throw new UserException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        String userId = String.valueOf(extractedUserId);

        // Redis에 저장된 RefreshToken과 비교
        String storedRefreshToken = redisUtil.getRefreshToken(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        if (!storedRefreshToken.equals(refreshToken)) {
            throw new UserException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        redisUtil.deleteRefreshToken(userId);
        redisUtil.saveRefreshToken(userId, newRefreshToken);

        return UserConverter.toSignInResponseDTO(newAccessToken, newRefreshToken);
    }

    /**
     * 이메일 중복 확인
     */
    @Override
    public void checkEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        }
    }

    /**
     * 요청에서 JWT 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}
