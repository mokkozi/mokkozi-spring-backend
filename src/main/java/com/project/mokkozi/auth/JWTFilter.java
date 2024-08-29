package com.project.mokkozi.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info(":: JWTFilter Start ::");

        // header 내 token 값 추출
        String token = jwtProvider.resolveToken(request);

        // token 값이 올바른지 판별
        if(token != null && jwtProvider.validateToken(token)) {
            log.info("== token : {} ==", token);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext(); // Spring Security에서 인증된 사용자임을 알 수 있도록 설정 진행
            CustomUserDetails customUserDetails = jwtProvider.getUserDetailsLoginId(token); // CustomUserDetilas 를 활용하여 사용자 정보를 갖고옴

            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword());
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
        }

        // filter 에서 thorws 를 하게 될 경우, 다른 정상적인 기능들도 수행이 불가능해질 염려가 있기 때문에 filterChain.doFilter 로 다음 필터가 실행되도록 넘김
        filterChain.doFilter(request, response);

    }
}
