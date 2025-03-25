package dgu.sw.domain.auth.controller;

import dgu.sw.domain.auth.dto.AuthDTO.AuthRequest.KakaoLoginRequest;
import dgu.sw.domain.auth.dto.AuthDTO.AuthResponse.AuthUserResponse;
import dgu.sw.domain.auth.service.AuthService;
import dgu.sw.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth 컨트롤러", description = "소셜 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 로그인 API", description = "카카오 SDK access token 기반 로그인 API")
    public ApiResponse<AuthUserResponse> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return ApiResponse.onSuccess(authService.kakaoLoginWithAccessToken(request.getAccessToken()));
    }
}
