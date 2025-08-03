package dgu.sw.domain.notification.scheduler;

import dgu.sw.domain.notification.service.NotificationService;
import dgu.sw.domain.quiz.entity.MockTest;
import dgu.sw.domain.quiz.repository.MockTestRepository;
import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {
    private final UserRepository userRepository;
    private final MockTestRepository mockTestRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *") // 매일 오전 8시
    public void sendDailyMockTestReminder() {
        log.info("🌅 [Scheduler] ✅ sendDailyMockTestReminder() 실행됨 at {}", LocalDateTime.now());

        List<User> users = userRepository.findAll();
        log.info("👥 전체 유저 수: {}", users.size());

        for (User user : users) {
            if (user.getFcmToken() == null || user.getFcmToken().isBlank()) {
                log.warn("⚠️ userId={} 의 fcmToken이 없습니다. 알림 건너뜁니다.", user.getUserId());
                continue;
            }

            MockTest latest = mockTestRepository
                    .findTopByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                    .orElse(null);

            try {
                if (latest != null
                        && latest.getCreatedAt().toLocalDate().isEqual(LocalDate.now().minusDays(1))
                        && latest.isCompleted()) {
                    notificationService.sendNotification(
                            user,
                            "📌 오늘도 풀어보세요!",
                            "어제도 잘 푸셨네요 👏"
                    );
                    log.info("✅ userId={} 에게 칭찬 메시지 전송 완료", user.getUserId());
                } else {
                    notificationService.sendNotification(
                            user,
                            "📌 오늘은 꼭 풀어보자!",
                            "모의고사 풀고 실력을 점검해요!"
                    );
                    log.info("✅ userId={} 에게 기본 리마인더 메시지 전송 완료", user.getUserId());
                }
            } catch (Exception e) {
                log.error("❌ userId={} 알림 전송 중 예외 발생: {}", user.getUserId(), e.getMessage(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시
    public void sendInactivityNotification() {
        log.info("🕘 [Scheduler] ✅ sendInactivityNotification() 실행됨 at {}", LocalDateTime.now());

        List<User> users = userRepository.findAll();
        log.info("👥 전체 유저 수: {}", users.size());
        int N = 3;

        for (User user : users) {
            if (user.getFcmToken() == null || user.getFcmToken().isBlank()) {
                log.warn("⚠️ userId={} 의 fcmToken이 없습니다. 알림 건너뜁니다.", user.getUserId());
                continue;
            }

            MockTest latest = mockTestRepository
                    .findTopByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                    .orElse(null);

            if (latest == null
                    || latest.getCreatedAt().toLocalDate().isBefore(LocalDate.now().minusDays(N))) {
                try {
                    notificationService.sendNotification(
                            user,
                            "⏰ 모의고사 미풀이",
                            "최근 " + N + "일간 미풀이 상태입니다. 지금 도전해보세요!"
                    );
                    log.info("✅ userId={} 에게 미풀이 알림 전송 완료", user.getUserId());
                } catch (Exception e) {
                    log.error("❌ userId={} 미풀이 알림 전송 중 예외 발생: {}", user.getUserId(), e.getMessage(), e);
                }
            }
        }
    }
}