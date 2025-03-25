package dgu.sw.domain.auth.service;

import dgu.sw.domain.auth.dto.AuthDTO.AuthResponse.AuthUserResponse;

public interface AuthService {
    AuthUserResponse kakaoLoginWithAccessToken(String accessToken);
}
