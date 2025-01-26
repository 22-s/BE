package dgu.sw.global.config.filter;

import dgu.sw.global.config.util.JWTUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = resolveToken(request);
        String refreshToken = resolveRefreshToken(request);

        if (accessToken == null && refreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (accessToken != null) {
            try {
                handleAccessToken(request, response, accessToken, refreshToken);
            } catch (JwtException | IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        } else {
            try {
                handleNoAccessToken(response, refreshToken);
            } catch (JwtException | IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh Token Expired");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleNoAccessToken(HttpServletResponse response, String refreshToken) throws IOException {
        if (refreshToken != null && jwtUtil.isRefreshTokenValid(refreshToken)) {
            String newAccessToken = jwtUtil.renewAccessToken(refreshToken);
            jwtUtil.setCookie(response, "accessToken", newAccessToken, 1800);

            String userId = jwtUtil.extractUserId(newAccessToken);
            validateAndSetAuthentication(newAccessToken, userId);
        } else {
            throw new IllegalArgumentException("Refresh Token is invalid or expired");
        }
    }

    private void handleAccessToken(HttpServletRequest request, HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        String blacklist = redisTemplate.opsForValue().get(accessToken);
        if (blacklist != null) {
            throw new IllegalArgumentException("Access Token is invalid (blacklisted)");
        }

        if (jwtUtil.isTokenExpired(accessToken)) {
            if (refreshToken != null) {
                handleNoAccessToken(response, refreshToken);
            } else {
                throw new JwtException("Access Token has expired");
            }
        } else {
            String userId = jwtUtil.extractUserId(accessToken);
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Authentication authentication = jwtUtil.getAuthentication(userId);
                SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext 설정
            }
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization-refresh");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    private void validateAndSetAuthentication(String token, String userId) {
        String blacklist = redisTemplate.opsForValue().get(token);
        if (blacklist != null) {
            throw new IllegalArgumentException("Token is invalid (logged-out or deleted user)");
        }
        Authentication authentication = jwtUtil.getAuthentication(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}