package dgu.sw.domain.user.service;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.*;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    SignUpResponse signUp(SignUpRequest request);

    SignInResponse signIn(HttpServletResponse response, SignInRequest request);

    void signOut(HttpServletRequest request, HttpServletResponse response);

    SignInResponse refreshAccessToken(String refreshToken, HttpServletResponse response);

    void checkEmailDuplicate(String email);

    // 비밀번호 변경
    // 이메일 유효성 검사
    void verifyEmailForPasswordReset(String email);

    // 이메일이 유효하다면, 해당 이메일로 인증코드 발송
    void sendVerificationCode(String email);

    // 인증코드 검증
    void verifyVerificationCode(String email, String code);

    // 비밀번호 변경, 확인
    void resetPassword(PasswordResetRequest request);

    MyPageResponse getMyPage(String userId);
}
