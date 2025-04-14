package dgu.sw.domain.notification.dto;

import dgu.sw.domain.notification.entity.Notification;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long id,
        String title,
        String body,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getNotificationId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}