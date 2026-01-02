package dgu.sw.global.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class FirebaseInitializer {

    @PostConstruct
    public void initialize() {
        try {
            // 1. Cloudtype 환경변수에서 JSON 내용 전체를 String으로 가져옴
            String firebaseConfig = System.getenv("FIREBASE_SERVICE_ACCOUNT");

            if (firebaseConfig == null || firebaseConfig.isEmpty()) {
                log.error("환경변수 [FIREBASE_SERVICE_ACCOUNT]가 설정되지 않았습니다. 배포 환경을 확인해주세요.");
                return; // 초기화 중단
            }

            // 2. 가져온 String(JSON)을 메모리 상의 InputStream으로 변환
            InputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

            // 3. Stream을 통해 인증 정보 로드
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // 4. Firebase 초기화
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized using Environment Variable.");
            } else {
                log.info("Firebase application already initialized.");
            }

        } catch (IOException e) {
            log.error("Firebase initialization error: {}", e.getMessage());
        }
    }
}