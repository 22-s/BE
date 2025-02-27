package dgu.sw.domain.auth.controller;

import dgu.sw.domain.auth.dto.AuthDTO.AuthResponse.AuthUserResponse;
import dgu.sw.domain.auth.service.AuthService;
import dgu.sw.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth 컨트롤러", description = "소셜 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao/login/{code}")
    @Operation(summary = "카카오 로그인 API", description = "카카오 로그인 API 입니다.")
    public ApiResponse<AuthUserResponse> kakaoLogin(@PathVariable String code) {
        return ApiResponse.onSuccess(authService.oAuthLogin(code));
    }
}
