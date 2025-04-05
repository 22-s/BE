package dgu.sw.domain.user.converter;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.MyPageResponse;
import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignUpResponse;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignUpRequest;
import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignInResponse;
import dgu.sw.domain.user.entity.User;

public class UserConverter {
    public static SignUpResponse toSignUpResponseDTO(User user) {
        return SignUpResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }

    public static SignInResponse toSignInResponseDTO(String accessToken, String refreshToken) {
        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static User toUser(SignUpRequest signUpRequest, String password) {
        return User.builder()
                .nickname(signUpRequest.getNickname())
                .email(signUpRequest.getEmail())
                .password(password)
                .joinDate(signUpRequest.getJoinDate())
                .build();
    }

    public static MyPageResponse toMyPageResponse(User user) {
        return MyPageResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .joinDate(user.getJoinDate())
                .profileImage(user.getProfileImage())
                .build();
    }
}
