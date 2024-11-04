package dgu.sw.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll() // Allow all requests without authentication
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                new AntPathRequestMatcher("/"),
//                                new AntPathRequestMatcher("/api/v0/auth/**"),
//                                new AntPathRequestMatcher("/swagger-ui.html"),
//                                new AntPathRequestMatcher("/swagger-ui/**"),
//                                new AntPathRequestMatcher("/v3/api-docs/**"),
//                                new AntPathRequestMatcher("/api-docs/**"),
//                                new AntPathRequestMatcher("/health"),
//                        ).permitAll() // 이 경로들은 모두 인증 없이 접근 가능
//                        .anyRequest().authenticated() // 그 외의 모든 경로는 인증 필요
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return httpSecurity.build();
    }
}
