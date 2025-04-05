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

        // 비밀번호 변경 - 이메일 유효성 검사
        // Controller에서 클라이언트 요청을 받을 때 사용되는 요청 DTO
        @Getter
        public static class PasswordEmailRequest {
            @NotBlank
            @Schema(description = "비밀번호 변경 요청 이메일", example = "hihi@gmail.com")
            private String email;
        }

        // 이메일이 유효하다면, 해당 이메일로 인증코드 발송
        @Getter
        public static class EmailSendRequest {
            @NotBlank
            @Schema(description = "인증코드 전송 대상 이메일", example = "hihi@gmail.com")
            private String email;
        }

        // 인증코드 검증
        @Getter
        public static class CodeVerificationRequest {
            @NotBlank
            @Schema(description = "인증코드 검증 대상 이메일", example = "hihi@gmail.com")
            private String email;

            @NotBlank
            @Schema(description = "사용자가 입력한 인증코드", example = "123456")
            private String code;
        }

        // 새 비밀번호 입력, 새 비밀번호 확인
        @Getter
        public static class PasswordResetRequest {
            @NotBlank
            @Schema(description = "비밀번호 변경 대상 이메일", example = "hihi@gmail.com")
            private String email;

            @NotBlank
            @Schema(description = "새 비밀번호", example = "newpassword123")
            private String newPassword;

            @NotBlank
            @Schema(description = "새 비밀번호 확인", example = "newpassword123")
            private String confirmPassword;
        }

        @Getter
        public static class RegisterJoinDateRequest {
            @NotNull
            @Schema(description = "등록할 입사일", example = "2025-04-01")
            private LocalDate joinDate;
        }

        @Getter
        public static class UpdateJoinDateRequest {
            @NotNull
            @Schema(description = "변경할 입사일", example = "2025-03-14")
            private LocalDate joinDate;
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

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MyPageResponse {
            @Schema(description = "유저 이름", example = "김동국")
            private String nickname;

            @Schema(description = "유저 이메일", example = "2025123456@dgu.ac.kr")
            private String email;

            @Schema(description = "입사일", example = "2025-03-14")
            private LocalDate joinDate;

            @Schema(description = "프로필 이미지 URL", example = "http://example.com/image.jpg")
            private String profileImage;
        }


        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class UpdateJoinDateResponse {
            @Schema(description = "변경된 입사일", example = "2025-03-14")
            private LocalDate updatedJoinDate;
        }
    }
}
