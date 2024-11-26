package dgu.sw.global.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int CODE_LENGTH_MIN = 8;
    private static final int CODE_LENGTH_MAX = 12;
    private static final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom random = new SecureRandom();

    public void saveRefreshToken(String userId, String refreshToken) {
        String key = generateKey(userId);
        redisTemplate.opsForValue().set(key, refreshToken, 14, TimeUnit.DAYS); // 14일간 유지
    }

    // userId로 키 생성
    private String generateKey(String userId) {
        return userId;
    }

    public void deleteEmailVerificationCode(String email) {
        redisTemplate.delete(email);
    }
}
