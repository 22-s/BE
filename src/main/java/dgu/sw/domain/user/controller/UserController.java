package dgu.sw.domain.user.controller;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignInResponse;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.EmailRequest;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignInRequest;
import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignUpResponse;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignUpRequest;
import dgu.sw.domain.user.service.UserService;
import dgu.sw.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @Operation(summary = "로그아웃 API", description = "로그아웃 API 입니다.")
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
}
