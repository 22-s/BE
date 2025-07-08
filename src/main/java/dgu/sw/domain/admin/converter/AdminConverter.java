package dgu.sw.domain.admin.converter;

import dgu.sw.domain.admin.dto.AdminDTO.AdminResponse.AdminUserResponse;
import dgu.sw.domain.user.entity.User;

public class AdminConverter {
    public static AdminUserResponse toAdminUserResponse(User user) {
        return AdminUserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
