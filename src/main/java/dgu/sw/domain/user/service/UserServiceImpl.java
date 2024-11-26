package dgu.sw.domain.user.service;

import dgu.sw.domain.user.converter.UserConverter;
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
    public void signIn(HttpServletResponse response, SignInRequest signInRequest) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UserException(ErrorStatus.INVALID_CREDENTIALS));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new UserException(ErrorStatus.INVALID_CREDENTIALS);
        }

        // AccessToken & RefreshToken 생성
        String accessToken = jwtUtil.generateAccessToken(String.valueOf(user.getUserId()));
        String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getUserId()));

        // Redis에 RefreshToken 저장
        redisUtil.saveRefreshToken(user.getEmail(), refreshToken);

        jwtUtil.setCookie(response, "accessToken", accessToken, 1800); // 30분
        jwtUtil.setCookie(response, "refreshToken", refreshToken, 604800); // 1주일
    }

    @Override
    public void signOut(HttpServletRequest request, HttpServletResponse response) {

        processToken(request, response);

        String accessToken = resolveToken(request);
        Long expiration = jwtUtil.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logoutUser", expiration, TimeUnit.MILLISECONDS);

        String userId = jwtUtil.extractUserId(accessToken);

        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserId(Long.valueOf(userId)));
    }

    private void processToken(HttpServletRequest request, HttpServletResponse response) {
        // 로그아웃 or 탈퇴 처리 하고 싶은 토큰이 유효한지 확인
        String accessToken = resolveToken(request);
        if (accessToken == null) {
            throw new UserException(ErrorStatus.TOKEN_NOT_FOUND);
        }
        if (jwtUtil.isTokenExpired(accessToken)) {
            throw new UserException(ErrorStatus.TOKEN_EXPIRED);
        }

        // Redis에 해당 Refresh Token 이 있는지 여부를 확인 후에 있을 경우 삭제
        String userId = jwtUtil.extractUserId(accessToken);

        if (redisTemplate.opsForValue().get(userId) != null) {
            redisTemplate.delete(userId);
        }

        jwtUtil.setCookie(response, "accessToken", null, 0);
        jwtUtil.setCookie(response, "refreshToken", null, 0);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = resolveTokenFromCookies(request);
        if (token == null) {
            token = resolveTokenFromHeader(request);
        }
        return token;
    }

    private String resolveTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String resolveTokenFromHeader(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}
