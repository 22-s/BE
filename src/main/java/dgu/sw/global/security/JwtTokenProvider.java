package dgu.sw.global.security;

import dgu.sw.domain.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT 토큰을 생성 및 관리하는 클래스
 * - AccessToken 및 RefreshToken을 생성
 * - RefreshToken을 Redis에 저장하여 관리
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKeyString;

    @Value("${spring.jwt.expiration}")
    private long jwtExpirationInMs;

    @Value("${spring.jwt.refreshExpiration}")
    private long jwtRefreshExpirationInMs;

    private SecretKey secretKey;
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 생성 (userId를 Subject로 설정)
    public String generateAccessToken(User user) {
        return createToken(user.getUserId(), user.getEmail(), user.getRole().toString(), jwtExpirationInMs);
    }

    // RefreshToken 생성 및 Redis 저장
    public String generateRefreshToken(User user) {
        String token = createToken(user.getUserId(), user.getEmail(), user.getRole().toString(), jwtRefreshExpirationInMs);
        redisTemplate.opsForValue().set(String.valueOf(user.getUserId()), token, jwtRefreshExpirationInMs, TimeUnit.MILLISECONDS);
        return token;
    }

    // JWT 생성 로직 (userId를 Subject로 설정)
    private String createToken(Long userId, String email, String role, long expirationMs) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
}
