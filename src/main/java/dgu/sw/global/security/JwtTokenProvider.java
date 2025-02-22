package dgu.sw.global.security.jwt;

import dgu.sw.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return createToken(user.getEmail(), jwtExpirationInMs);
    }

    public String generateRefreshToken(User user) {
        String token = createToken(user.getEmail(), jwtRefreshExpirationInMs);
        redisTemplate.opsForValue().set(user.getEmail(), token, jwtRefreshExpirationInMs, TimeUnit.MILLISECONDS);
        return token;
    }

    private String createToken(String email, long expirationMs) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication getAuthentication(String token) {
        String email = extractEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }
}
