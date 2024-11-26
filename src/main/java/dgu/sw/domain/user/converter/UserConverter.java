package dgu.sw.domain.user.converter;

import dgu.sw.domain.user.dto.UserDTO.UserResponse.SignUpResponse;
import dgu.sw.domain.user.dto.UserDTO.UserRequest.SignUpRequest;
import dgu.sw.domain.user.entity.User;

public class UserConverter {
    public static SignUpResponse toSignUpResponseDTO(User user) {
        return SignUpResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
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
}
