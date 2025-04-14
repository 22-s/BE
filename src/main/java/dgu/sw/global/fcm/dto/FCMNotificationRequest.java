package dgu.sw.global.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FCMNotificationRequest {

    private String targetToken;  // 푸시를 받을 대상 기기 토큰
    private String title;        // 알림 제목
    private String body;         // 알림 내용
}