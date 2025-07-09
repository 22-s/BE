package dgu.sw.domain.notification.service;

import dgu.sw.domain.notification.dto.NotificationResponse;
import dgu.sw.domain.user.entity.User;

import java.util.List;

public interface NotificationService {

    void sendNotification(User user, String title, String body);

    List<NotificationResponse> getNotifications(User user);

    void markAsRead(Long notificationId, User user);
}