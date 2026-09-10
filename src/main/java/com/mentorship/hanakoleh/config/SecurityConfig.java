package com.mentorship.hanakoleh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TEMPORARY development security configuration.
 *
 * <p>Permits every request (Swagger UI, OpenAPI docs and the REST API) and disables
 * CSRF so the API can be exercised without logging in. Defining this bean also makes
 * Spring Boot's default security back off, so there is no generated login password.
 *
 * <p>This exists only until real authentication lands
 * (issue 02USER-add-user-authentication), which should replace it with a proper
 * stateless JWT filter chain that actually secures the endpoints.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
