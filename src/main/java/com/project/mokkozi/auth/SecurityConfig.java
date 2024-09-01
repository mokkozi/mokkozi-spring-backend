package com.project.mokkozi.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        try {
            httpSecurity
                    .authorizeHttpRequests(auth -> auth.requestMatchers("/members/login").permitAll()
                            .requestMatchers("/").hasAnyAuthority("ADMIN", "USER")
                            .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                            .requestMatchers("/**").permitAll()
                            .anyRequest().authenticated())
                    .csrf(csrf -> csrf.disable())
                    .headers(headers -> headers.disable())
                    .logout(logout -> logout.disable())
                    .anonymous(anonymous -> anonymous.disable())
                    .requestCache(cache -> cache.disable())
                    .securityContext(context -> context.disable())
                    .sessionManagement(session -> session.disable());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpSecurity.build();
    }
}
