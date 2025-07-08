package dgu.sw.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
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
        // 1. JwtAuthenticationToken으로 캐스팅
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;

        // 2. userId (principal) 와 role (credentials) 추출
        Long userId = (Long) jwtToken.getPrincipal();
        String role = (String) jwtToken.getCredentials();

        if (userId == null || role == null) {
            throw new BadCredentialsException("Missing authentication information.");
        }

        // 3. DB에서 사용자 조회
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUserId(userId);

        if (!userDetails.getRole().equals(role)) {
            throw new BadCredentialsException("Invalid Role");
        }

        // 4. 인증 완료 토큰 반환
        return new JwtAuthenticationToken(userDetails, role, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
