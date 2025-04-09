package dgu.sw.domain.notification.service;

import dgu.sw.domain.notification.dto.NotificationResponse;
import dgu.sw.domain.notification.entity.Notification;
import dgu.sw.domain.notification.repository.NotificationRepository;
import dgu.sw.domain.user.entity.User;
import dgu.sw.global.fcm.service.FCMService;
import dgu.sw.global.exception.NotificationException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;

    @Override
    public void sendNotification(User user, String title, String body) {
        // 1. 알림 엔티티 저장
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .body(body)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // 2. FCM 전송
        fcmService.sendMessageTo(
                new FCMService.NotificationTarget(user.getFcmToken(), title, body)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(NotificationResponse::from)
                .collect(toList());
    }

    @Override
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        if (!notification.getUser().getUserId().equals(user.getUserId())) {
            throw new NotificationException(ErrorStatus.NOTIFICATION_ACCESS_DENIED);
        }

        notification.markAsRead();
    }
}