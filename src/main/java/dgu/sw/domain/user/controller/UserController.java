package dgu.sw.domain.user.controller;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.*;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.*;
import dgu.sw.domain.user.service.UserService;
import dgu.sw.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User 컨트롤러", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "회원가입 API 입니다.")
    public ApiResponse<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ApiResponse.onSuccess(userService.signUp(signUpRequest));
    }

    @PostMapping("/signin")
    @Operation(summary = "로그인 API", description = "로그인 API 입니다.")
    public ApiResponse<SignInResponse> signIn(HttpServletResponse response, @RequestBody @Valid SignInRequest signInRequest) {
        return ApiResponse.onSuccess(userService.signIn(response, signInRequest));
    }

    @PostMapping("/signout")
    @Operation(summary = "로그아웃 API", description = "일반 및 소셜 통합 로그아웃 API입니다.")
    public ApiResponse<String> signOut(HttpServletRequest request, HttpServletResponse response) {
        userService.signOut(request, response);
        return ApiResponse.onSuccess("로그아웃 성공");
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신 API", description = "Refresh Token을 이용하여 Access Token을 갱신합니다.")
    public ApiResponse<SignInResponse> refresh(@RequestHeader("refreshToken") String refreshToken, HttpServletResponse response) {
        return ApiResponse.onSuccess(userService.refreshAccessToken(refreshToken, response));
    }

    @PostMapping("/check-email")
    @Operation(summary = "이메일 중복 확인 API", description = "이메일 중복 여부를 확인합니다.")
    public ApiResponse<String> checkEmailDuplicate(@RequestBody @Valid EmailRequest emailRequest) {
        userService.checkEmailDuplicate(emailRequest.getEmail());
        return ApiResponse.onSuccess("사용 가능한 이메일입니다.");
    }

    // 비밀번호 변경하기
    // 이메일 유효성 검사
    @PostMapping("/password/verify-email")
    @Operation(summary = "비밀번호 변경 - 이메일 유효성 확인", description = "입력한 이메일이 회원가입된 이메일인지 확인합니다.")
    public ApiResponse<String> verifyEmailForPasswordReset(@RequestBody @Valid PasswordEmailRequest request) {
        userService.verifyEmailForPasswordReset(request.getEmail());
        return ApiResponse.onSuccess("해당 이메일은 존재하는 회원입니다.");
    }

    // 이메일이 유효하다면, 해당 이메일로 인증코드 발송
    @PostMapping("/password/send-code")
    @Operation(summary = "비밀번호 변경 - 인증코드 이메일 발송", description = "이메일로 인증코드를 발송합니다.")
    public ApiResponse<String> sendVerificationCode(@RequestBody @Valid EmailSendRequest request) {
        userService.sendVerificationCode(request.getEmail());
        return ApiResponse.onSuccess("인증코드가 이메일로 발송되었습니다.");
    }

    // 인증코드 검증 : 사용자가 인증코드 입력 시, 발송한 인증코드와 일치하는지 확인
    @PostMapping("/password/verify-code")
    @Operation(summary = "비밀번호 변경 - 인증코드 검증", description = "사용자가 입력한 인증코드를 검증합니다.")
    public ApiResponse<String> verifyCode(@RequestBody @Valid CodeVerificationRequest request) {
        userService.verifyVerificationCode(request.getEmail(), request.getCode());
        return ApiResponse.onSuccess("인증 성공");
    }

    // 새 비밀번호 입력, 새 비밀번호 확인
    @PostMapping("/password/reset")
    @Operation(summary = "비밀번호 변경", description = "새 비밀번호로 변경합니다.")
    public ApiResponse<String> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        userService.resetPassword(request);
        return ApiResponse.onSuccess("비밀번호가 성공적으로 변경되었습니다.");
    }

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회", description = "유저의 마이페이지 정보를 반환합니다.")
    public ApiResponse<MyPageResponse> getMyPage(Authentication authentication) {
        return ApiResponse.onSuccess(userService.getMyPage(authentication.getName()));
    }

    @PostMapping("/join-date")
    @Operation(summary = "입사일 최초 등록 API", description = "소셜 로그인 유저의 최초 입사일을 등록합니다. 이미 존재하면 400 반환.")
    public ApiResponse<UpdateJoinDateResponse> registerJoinDate(
            Authentication authentication,
            @RequestBody @Valid RegisterJoinDateRequest request
    ) {
        UpdateJoinDateResponse response = userService.registerJoinDate(authentication.getName(), request.getJoinDate());
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/join-date")
    @Operation(summary = "입사일 변경 API", description = "사용자의 입사일을 수정합니다.")
    public ApiResponse<UpdateJoinDateResponse> updateJoinDate(
            Authentication authentication,
            @RequestBody @Valid UpdateJoinDateRequest request
    ) {
        UpdateJoinDateResponse response = userService.updateJoinDate(authentication.getName(), request.getJoinDate());
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원탈퇴 API", description = "일반 및 소셜 통합 회원탈퇴 API입니다.")
    public ApiResponse<String> withdraw(HttpServletRequest request) {
        userService.withdraw(request);
        return ApiResponse.onSuccess("회원탈퇴 완료");
    }
}
