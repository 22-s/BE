package dgu.sw.domain.user.service;

import dgu.sw.domain.user.converter.UserConverter;
import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignInResponse;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import dgu.sw.global.config.redis.RedisUtil;
import dgu.sw.global.config.util.JWTUtil;
import dgu.sw.global.exception.UserException;
import dgu.sw.global.status.ErrorStatus;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignUpResponse;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignUpRequest;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignInRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        // 사용자 생성
        User user = UserConverter.toUser(signUpRequest, encryptedPassword);
        userRepository.save(user);

        return UserConverter.toSignUpResponseDTO(user);
    }

    @Override
    public SignInResponse signIn(HttpServletResponse response, SignInRequest signInRequest) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new UserException(ErrorStatus.INVALID_CREDENTIALS);
        }

        // AccessToken & RefreshToken 생성
        String accessToken = jwtUtil.generateAccessToken(String.valueOf(user.getUserId()));
        String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getUserId()));

        // Redis에 RefreshToken 저장
        redisUtil.saveRefreshToken(String.valueOf(user.getUserId()), refreshToken);

        return UserConverter.toSignInResponseDTO(accessToken, refreshToken);
    }

    @Override
    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        // AccessToken 해제 처리
        String accessToken = resolveToken(request);

        if (accessToken == null) {
            throw new UserException(ErrorStatus.TOKEN_NOT_FOUND);
        }
        // AccessToken 만료 시간 가져오기
        Long expiration = jwtUtil.getExpiration(accessToken);

        // Redis에 AccessToken을 블랙리스트로 등록
        redisTemplate.opsForValue().set(accessToken, "logoutUser", expiration, TimeUnit.MILLISECONDS);

        // Redis에서 RefreshToken 삭제
        String userId = jwtUtil.extractUserId(accessToken);
        redisUtil.deleteRefreshToken(userId);
    }

    @Override
    public SignInResponse refreshAccessToken(String refreshToken) {
        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
            throw new UserException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }
        String userId = jwtUtil.extractUserId(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);

        redisUtil.saveRefreshToken(userId, newRefreshToken);

        return UserConverter.toSignInResponseDTO(newAccessToken, newRefreshToken);
    }

    private String resolveToken(HttpServletRequest request) {
        // 요청 헤더에서 AccessToken 추출
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    @Override
    public void checkEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        }
    }
}
