package io.techchamps.restbackend.config;

import io.techchamps.restbackend.jwt.JwtAuthEntryPoint;
import io.techchamps.restbackend.jwt.JwtAuthTokenFilter;
import io.techchamps.restbackend.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthEntryPoint unauthorizedHandler;
    private final JwtAuthTokenFilter authenticationJwtTokenFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService,
                             JwtAuthEntryPoint unauthorizedHandler,
                             JwtAuthTokenFilter authenticationJwtTokenFilter,
                             CorsConfigurationSource corsConfigurationSource) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.authenticationJwtTokenFilter = authenticationJwtTokenFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Configure headers
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // Disable frame options for H2 console
                )

                // Authorize HTTP requests
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/**").permitAll() // Adjust for your use case
                        .anyRequest().authenticated()
                )

                // Exception handling
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(unauthorizedHandler)
                )

                // Session management
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Enable CORS using the configuration source
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

