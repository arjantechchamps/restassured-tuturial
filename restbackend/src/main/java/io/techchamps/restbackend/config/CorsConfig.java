package io.techchamps.restbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static io.techchamps.restbackend.config.Constants.*;

@Configuration
public class CorsConfig {

    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setExposedHeaders(ALLOWED_EXPOSED_HEADERS);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}