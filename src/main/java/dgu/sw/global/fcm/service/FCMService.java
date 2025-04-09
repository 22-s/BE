package dgu.sw.global.fcm.service;

import com.google.firebase.messaging.*;
import dgu.sw.global.exception.UserException;
import dgu.sw.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    public void sendMessage(String targetToken, String title, String body) {
        try {
            // 메시지 구성
            Message message = Message.builder()
                    .setToken(targetToken)
                    .putData("title", title)
                    .putData("body", body)
                    .build();

            // Firebase 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 메시지 전송 완료: {}", response);

        } catch (FirebaseMessagingException e) {
            log.error("FCM 메시지 전송 실패: {}", e.getMessage());
            throw new UserException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    public void sendMessageTo(NotificationTarget target) {
        sendMessage(target.targetToken(), target.title(), target.body());
    }

    public record NotificationTarget(String targetToken, String title, String body) {}
}