package dgu.sw.global.config.util;

import dgu.sw.domain.user.entity.User;
import dgu.sw.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Slf4j
@Configuration
public class JWTUtil {

    @Value("${spring.jwt.secret}")
    private String secretKeyString;

    @Value("${spring.jwt.expiration}")
    private long jwtExpirationInMs;

    @Value("${spring.jwt.refreshExpiration}")
    private long jwtRefreshExpirationInMs;

    private SecretKey secretKey;

    private final RedisTemplate<String, String> redisTemplate;

    private String accessHeader = "Authorization";
    private String refreshHeader = "Authorization-refresh";

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String userId) {
        return generateToken(userId, jwtExpirationInMs);
    }

    public String generateRefreshToken(String userId) {
        return generateToken(userId, jwtRefreshExpirationInMs);
    }

    private String generateToken(String userId, long expirationMs) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expirationDateTime = now.plusSeconds(expirationMs / 1000);

        Claims claims = Jwts.claims();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(expirationDateTime.toInstant()))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // AccessToken 갱신
    public String renewAccessToken(String refreshToken) {
        String userId = extractUserId(refreshToken);

        if (userId != null && !isTokenExpired(refreshToken)) {
            return generateAccessToken(userId);
        }
        return null;
    }

    public Authentication getAuthentication(String userId) {
        Optional<User> optionalUser = userRepository.findById(Long.valueOf(userId));

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getUserId().toString(),
                    "",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

        return null; // 사용자 없을 경우 null 반환
    }


    public boolean isRefreshTokenValid(String refreshToken) {
        String tokenFromRedis = redisTemplate.opsForValue().get(extractUserId(refreshToken));
        return tokenFromRedis != null && tokenFromRedis.equals(refreshToken);
    }

    //JWT 토큰의 만료시간
    public Long getExpiration(String accessToken){

        Date expiration = Jwts.parserBuilder().setSigningKey(secretKey)
                .build().parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    public void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .sameSite("None")
                .httpOnly(false)
                .secure(true)
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
