package dgu.sw.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080", "http://localhost:3000", "http://localhost:8081", "http://10.0.2.2:8080", "https://22s.store", "http://127.0.0.1:8080")
                        .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

        };
    }
}
