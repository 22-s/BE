package dgu.sw.domain.user.service;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.*;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;

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

    UpdateJoinDateResponse updateJoinDate(String userId, LocalDate joinDate);

    void withdraw(HttpServletRequest request);

    UpdateJoinDateResponse registerJoinDate(String userId, LocalDate joinDate);
    // 클라이언트 입장에서 응답은 같으니 Register용을 따로 만들지 않고 Update를 사용!
}
