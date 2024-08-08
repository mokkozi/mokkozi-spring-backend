package com.project.mokkozi.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebSecurity
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
                    .csrf(csrf -> csrf.ignoringRequestMatchers("/members/login"))
                    .headers(headers -> headers.disable())
                    .logout(logout -> logout.disable())
//                    .formLogin(form -> form.loginPage("/members/login").loginProcessingUrl("/members/login")
//                            .defaultSuccessUrl("/", true).permitAll())
                    .anonymous(anonymous -> anonymous.disable())
                    //  .cors(cors -> cors.disable())
                    .requestCache(cache -> cache.disable())
                    .securityContext(context -> context.disable())
                    .sessionManagement(session -> session.disable());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpSecurity.build();
    }
}
