package dgu.sw.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class UserDTO {
    public static class UserRequest {

        @Getter
        public static class SignUpRequest {
            @NotBlank
            @Schema(description = "유저 이름", example = "이름")
            private String nickname;

            @NotBlank
            @Schema(description = "유저 이메일", example = "이메일")
            private String email;

            @NotBlank
            @Schema(description = "유저 비밀번호", example = "비밀번호")
            private String password;

            @Schema(description = "유저 입사일", example = "2024-07-20")
            private LocalDate joinDate;
        }

        @Getter
        public static class SignInRequest {
            @NotBlank
            @Schema(description = "유저 이메일", example = "이메일")
            private String email;

            @NotBlank
            @Schema(description = "유저 비밀번호", example = "비밀번호")
            private String password;
        }

        @Getter
        public static class EmailRequest {
            @NotBlank
            @Schema(description = "유저 이메일", example = "이메일")
            private String email;
        }
    }

    public static class UserResponse {

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SignUpResponse {
            @Schema(description = "유저 이름", example = "이름")
            private String nickname;

            @Schema(description = "유저 이메일", example = "이메일")
            private String email;
        }

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SignInResponse {
            @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR...")
            private String accessToken;

            @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR...")
            private String refreshToken;
        }
    }
}
