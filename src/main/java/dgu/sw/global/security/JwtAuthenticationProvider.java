package dgu.sw.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

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
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
