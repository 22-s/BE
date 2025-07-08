package dgu.sw.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

/**
 * JWT 관련 유틸리티 클래스
 * - JWT 검증 및 추출, 파싱 기능 제공
 * - JWT를 HTTP 요청 및 응답에서 다룰 수 있도록 지원
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final Logger LOGGER = Logger.getLogger(JwtUtil.class.getName());

    @Value("${spring.jwt.secret}")
    private String secretKeyString;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // JWT에서 userId 추출
    public Long extractUserId(String token) {
        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            // subject가 숫자인지 확인
            if (!subject.matches("\\d+")) {
                throw new NumberFormatException("Invalid userId format in JWT: " + subject);
            }

            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid userId format in JWT: " + e.getMessage());
            return null;
        } catch (Exception e) {
            LOGGER.warning("Failed to extract userId from token: " + e.getMessage());
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class); // "role" 클레임에서 값을 추출
        } catch (Exception e) {
            LOGGER.warning("Failed to extract role from token: " + e.getMessage());
            return null;
        }
    }

    // JWT 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            LOGGER.warning("Invalid JWT Token: " + e.getMessage());
        }
        return false;
    }

    // HTTP 요청에서 Authorization 헤더에서 JWT 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    // JWT 만료 시간 반환 (밀리초 단위)
    public Long getExpiration(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expiration.getTime() - System.currentTimeMillis();
        } catch (JwtException e) {
            LOGGER.warning("Failed to get expiration time: " + e.getMessage());
            return null;
        }
    }

    // 쿠키 설정
    public void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
