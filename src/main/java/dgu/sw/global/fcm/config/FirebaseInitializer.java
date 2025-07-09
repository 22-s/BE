package dgu.sw.global.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseInitializer {

    @Value("${fcm.file-path}")
    private Resource firebaseConfig;

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseConfig.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options); // Firebase SDK 초기화
                log.info("Firebase application has been initialized.");
            } else {
                log.info("Firebase application already initialized.");
            }

        } catch (IOException e) {
            log.error("Firebase initialization error: {}", e.getMessage());
        }
    }
}