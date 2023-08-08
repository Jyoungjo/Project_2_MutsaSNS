package com.example.mutsasns.global.config;

import com.example.mutsasns.domain.user.service.JpaUserDetailsManager;
import com.example.mutsasns.global.filter.JwtAuthorizationFilter;
import com.example.mutsasns.global.filter.JwtAuthenticationFilter;
import com.example.mutsasns.global.jwt.entrypoint.CustomAuthenticationEntryPoint;
import com.example.mutsasns.global.jwt.provider.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;
    private final JpaUserDetailsManager userDetailsManager;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(auth ->
                auth
                        .requestMatchers("/api/users/login", "/api/users/register", "/static/**").permitAll()
                        .requestMatchers(
                                HttpMethod.GET, "/api/articles", "/api/users/**"
                        ).permitAll()
                        .anyRequest().authenticated()
        );

        http.exceptionHandling(ex -> ex.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        http.addFilterBefore(authorizationFilter(), JwtAuthenticationFilter.class)
            .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter authenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return jwtAuthenticationFilter;
    }

    @Bean
    public JwtAuthorizationFilter authorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsManager);
    }
}
