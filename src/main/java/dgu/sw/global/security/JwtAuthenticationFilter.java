package dgu.sw.global.security;

import dgu.sw.global.config.redis.RedisUtil;
import dgu.sw.global.exception.UserException;
import dgu.sw.global.status.ErrorStatus;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * - 매 요청마다 실행되며, 요청의 Authorization 헤더에서 JWT를 추출하고 검증함.
 * - JWT가 유효하면 AuthenticationManager를 사용하여 인증을 수행하고, SecurityContext에 저장함.
 * - Spring Security의 OncePerRequestFilter를 상속하여, 요청당 한 번만 실행되도록 보장함.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = jwtUtil.resolveToken(request);

            if (token != null) {
                // 블랙리스트에 등록된 토큰이면 인증 불가 처리
                if (redisUtil.getData(token).orElse("").equals("BLACKLIST")) {
                    throw new UserException(ErrorStatus.LOGGED_OUT_TOKEN);
                }

                if (jwtUtil.validateToken(token)) {
                    // 인증 토큰 생성하고 AuthenticationManager를 통해 인증 수행
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(token, null)
                    );
                    // 인증이 성공하면 SecurityContext에 저장하여 이후의 요청에서 인증 정보 활용 가능하게 함
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication error: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
