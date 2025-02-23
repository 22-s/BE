package dgu.sw.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * JWT 기반 인증을 수행하는 AuthenticationProvider 구현 클래스
 * - JWT 토큰에서 사용자 정보를 추출하여 인증을 처리함
 * - AuthenticationManager에 의해 호출되며, SecurityContext에 인증 객체를 저장함
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getPrincipal() == null) {
            throw new BadCredentialsException("Invalid authentication request: missing credentials.");
        }

        String token = (String) authentication.getPrincipal();
        Long userId = jwtUtil.extractUserId(token);

        if (userId == null) {
            throw new BadCredentialsException("Invalid token: cannot extract user ID.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUserId(userId);
        // 인증 객체 생성 및 반환 (SecurityContextHolder에 저장될 객체)
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
