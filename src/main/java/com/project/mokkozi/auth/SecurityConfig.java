package com.project.mokkozi.auth;

import com.project.mokkozi.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        try {
            httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)  // 세션을 사용하지 않고 JWT 토큰을 활용하여 진행, CSRF 토큰 검사 비활성화
                    .httpBasic(AbstractHttpConfigurer::disable) // HTTP 요청 시 'Authorization' 헤더에 사용자 이름과 비밀번호를 Base64로 인코딩하여 서버로 보내는 인증 방식
                    // JWT 기반의 인증 방식을 사용하여 더 안전하고 RESTful한 인증을 구현하기 위함
                    .authorizeHttpRequests(auth -> {    // 인증 절차에 대한 설정 진행
                        auth.requestMatchers("/", "/members/login").permitAll(); // 설정된 URL은 인증되지 않더라도 누구든 접근 가능
                        auth.requestMatchers(HttpMethod.POST, "/members").permitAll();  // (회원가입) POST 방식으로 들어온 해당 URL만 누구든 접근 가능
                        auth.requestMatchers("/admin/**").hasAnyAuthority(Role.ADMIN.getKey()); // ADMIN ROLE을 가진 사용자만 해당 URL 인가
                        auth.anyRequest().authenticated(); // 위 페이지 외는 인증이 되어야 접근 가능 (ROLE에 상관없이)
                    })
                    .headers(headers -> headers.disable()) // 사용시 외부 접근에 대한 보안 강화 (iframe 등) - 운영 시 활성화하면 될 것 같음
                   /* .formLogin(formLogin -> formLogin           // form 로그인 인증 기능이 동작
                                    .loginPage("/members/login")        // 사용자 정의 로그인 페이지 (인증없이 접근 가능)
                                    .defaultSuccessUrl("/mokkozis")             // 로그인 성공 후 이동 페이지
                                    .failureUrl("/members/login?fail")      // 로그인 실패 후 이동 페이지
                                    .permitAll())*/
                    // formLogin 주석 안 해두면 GET 으로 호출을 엄청 많이해서 먹통이 됨.. todo formLogin 주석 풀 경우 리다이렉트 호출 증가 이유
                    .logout(logout -> logout    // logout 설정
                            .logoutUrl("/members/logout")   // 로그아웃 시 맵핑되는 URL
                            .logoutSuccessUrl("/members/login")     // 로그아웃 성공 시 리다이렉트 URL
                    ) // 로그아웃 기능 허용
                    .anonymous(Customizer.withDefaults())   // 익명 사용자 활성화
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            ;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpSecurity.build();
    }
}
