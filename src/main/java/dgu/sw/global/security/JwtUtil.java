package dgu.sw.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

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

    /**
     * JWT 토큰에서 이메일(사용자 정보) 추출
     */
    public String extractEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            LOGGER.warning("Failed to extract email from token: " + e.getMessage());
            return null;
        }
    }

    /**
     * JWT 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.warning("Token expired: " + e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.warning("Invalid JWT token: " + e.getMessage());
        } catch (SignatureException e) {
            LOGGER.warning("Invalid JWT signature: " + e.getMessage());
        } catch (JwtException e) {
            LOGGER.warning("JWT Exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * HTTP 요청에서 Authorization 헤더에서 JWT 토큰 추출
     */
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    /**
     * 쿠키 설정
     */
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
