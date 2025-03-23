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
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignUpResponse;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignUpRequest;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignInRequest;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.PasswordResetRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dgu.sw.global.config.util.EmailService;

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

    private final EmailService emailService;

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

    // 비밀번호 변경
    // 이메일 유효성 검사
    @Override
    public void verifyEmailForPasswordReset(String email) {
        boolean exists = userRepository.existsByEmail(email);
        if (!exists) {
            throw new UserException(ErrorStatus.USER_NOT_FOUND);
        }
    }

    // 이메일이 유효하다면, 해당 이메일로 인증코드 발송
    @Override
    public void sendVerificationCode(String email) {
        // 1. 이메일 존재 여부 확인
        if (!userRepository.existsByEmail(email)) {
            throw new UserException(ErrorStatus.USER_NOT_FOUND);
        }

        // 2. 인증코드 생성 (6자리 숫자) - 랜덤
        String verificationCode = generateUniqueCode();

        // 3. Redis에 저장 (3분 유효)
        redisUtil.setDataExpire("password_code:" + email, verificationCode, 180); // 180초 = 3분

        // 4. 이메일 전송
        String subject = "[신입사UP] 비밀번호 변경 인증코드 안내";
        String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 8px;'>"
                + "<h2 style='color: #4CAF50;'>🔐 비밀번호 변경 인증코드</h2>"
                + "<p>안녕하세요, <strong>신입사UP</strong>입니다.</p>"
                + "<p>비밀번호 변경을 위한 인증코드가 요청되었습니다.</p>"
                + "<p style='font-size: 18px; margin-top: 20px;'>"
                + "👉 <strong style='font-size: 24px; letter-spacing: 2px;'>" + verificationCode + "</strong>"
                + "</p>"
                + "<p>아래 단계에 따라 인증을 완료해 주세요:</p>"
                + "<ol>"
                + "<li>신입사UP 앱 또는 웹사이트로 돌아가기</li>"
                + "<li>인증코드 입력란에 위 코드를 정확히 입력</li>"
                + "<li>비밀번호 변경 절차 진행</li>"
                + "</ol>"
                + "<p style='color: #999;'>⏰ 유효 시간: 3분</p>"
                + "<hr style='margin-top: 30px;'>"
                + "<p style='font-size: 12px; color: #888;'>본 메일은 발신 전용입니다. 인증을 요청하지 않았다면 이 메일을 무시해 주세요.</p>"
                + "<p style='font-size: 12px; color: #888;'>© 2025 신입사UP. All rights reserved.</p>"
                + "</div>";

        // 실제 이메일 발송 Util (아래는 임시로 console 출력 예시)
        // System.out.println("[TEST] 인증코드 발송됨: " + email + verificationCode);
        // 아래는 진짜 이메일 전송 - 실제는 추후 연결
        emailService.sendEmail(email, subject, htmlContent, true);
    }

    // 인증코드 발송 - 동시에 여러 사용자가 요청해도 중복 안 되도록 설정
    private String generateUniqueCode() {
        int maxAttempts = 10;
        String code;
        int attempts = 0;

        do {
            if (attempts++ >= maxAttempts) {
                throw new UserException(ErrorStatus._INTERNAL_SERVER_ERROR); // 혹은 CODE_GENERATION_FAILED 추가
            }
            code = redisUtil.generateRandomCode();
        } while (redisUtil.isCodeUsed(code));

        redisUtil.markCodeAsUsed(code, 180);
        return code;
    }

    // 인증코드 검증
    @Override
    public void verifyVerificationCode(String email, String code) {
        String redisKey = "password_code:" + email;
        String storedCode = redisUtil.getData(redisKey).orElseThrow(() -> new UserException(ErrorStatus.CODE_EXPIRED));
        // orElseThrow()를 통해 값이 없을 경우(null일 경우) 예외를 발생시킴

        if (!storedCode.equals(code)) {
            throw new UserException(ErrorStatus.CODE_MISMATCH); // 인증코드가 다르면 예외 발생
        }

        redisUtil.deleteKey(redisKey); // 인증 성공 시 삭제
    }

    // 비밀번호 변경, 확인
    @Override
    public void resetPassword(PasswordResetRequest request) {
        String email = request.getEmail();

        // 1. 사용자 존재 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        // 2. 새 비밀번호, 확인 일치 여부 체크
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new UserException(ErrorStatus.PASSWORD_NOT_MATCH);
        }

        // 3. 비밀번호 암호화 후 저장
        String encryptedPassword = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encryptedPassword);

        // 4. 저장 (JPA는 변경 감지로 자동 반영되지만 명시적으로 저장해도 좋음)
        userRepository.save(user);
    }
}