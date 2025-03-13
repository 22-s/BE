package dgu.sw.domain.user.service;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignInResponse;
import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignUpResponse;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignUpRequest;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignInRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    SignUpResponse signUp(SignUpRequest request);

    SignInResponse signIn(HttpServletResponse response, SignInRequest request);

    void signOut(HttpServletRequest request, HttpServletResponse response);

    SignInResponse refreshAccessToken(String refreshToken, HttpServletResponse response);

    void checkEmailDuplicate(String email);

}
