package dgu.sw.domain.notification.repository;

import dgu.sw.domain.notification.entity.Notification;
import dgu.sw.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}