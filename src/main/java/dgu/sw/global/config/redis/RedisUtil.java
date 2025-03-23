package dgu.sw.global.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Optional;
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

    // RefreshToken 가져오기 (Optional로 감싸서 Null 체크)
    public Optional<String> getRefreshToken(String userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(userId));
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(userId);
    }

    public void addTokenToBlacklist(String accessToken, long expiration) {
        redisTemplate.opsForValue().set(accessToken, "BLACKLIST", expiration, TimeUnit.MILLISECONDS);
    }

    // 인증코드 관련
    // 유효시간 확인
    public void setDataExpire(String key, String value, long expireTimeSec) {
        redisTemplate.opsForValue().set(key, value, expireTimeSec, TimeUnit.SECONDS);
    }

    // 랜덤 인증코드 생성
    public String generateRandomCode() {
        int code = (int) (Math.random() * 900000) + 100000; // 100000~999999
        return String.valueOf(code);
    }

    // 인증코드 조회
    public Optional<String> getData(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    // 인증코드 사용 여부 확인
    public boolean isCodeUsed(String code) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("CODE_USED:" + code));
    }

    // 인증코드 사용된 것으로 마킹
    public void markCodeAsUsed(String code, long ttlSeconds) {
        redisTemplate.opsForValue().set("CODE_USED:" + code, "USED", ttlSeconds, TimeUnit.SECONDS);
    }

    // 인증코드 인증 시 삭제
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}