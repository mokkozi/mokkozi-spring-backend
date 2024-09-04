package com.project.mokkozi.auth;

import com.project.mokkozi.dto.JWTDto;
import com.project.mokkozi.model.Member;
import com.project.mokkozi.service.MemberService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Component
public class JWTProvider implements AuthenticationProvider {

    private final Key secretKey;
    private final long ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 60; // 1시간
    private final long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60 * 60 * 24 * 7; // 7일
    private final MemberService memberService;

    @Autowired
    public JWTProvider(@Value("${jwt.secret.key}") String secret, MemberService memberService) {
        this.memberService = memberService;
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * AccessToken과 RefreshToken을 발급
     * <p>
     * @param member 입력된 사용자 정보
     * @return 현 시점으로 발급된 AccessToken과 RefreshToken (JWT)
     */
    public JWTDto generateAccessNRefreshToken(Member member) { // ,List<> roles
        Date now = new Date();
        Claims claims = Jwts.claims();
        claims.put("sub", member.getName());
        claims.put("jti", member.getLoginId());

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRED_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRED_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return JWTDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Token 내 사용자 정보를 권한 객체에 담아 전달
     * <p>
     * @param token 전달받은 Token
     * @return 현 사용자 정보+권한
     */
    // 추출한 사용자 정보를 인증 객체에 담아줌
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        User principal = new User(claims.getId(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    /**
     * Header의 Token 값 존재 유무 판별
     * <p>
     * @param request HttpServletRequest 값
     * @return header에 JWT가 정상적으로 들어갔을 경우 token 값만을 추출, 그렇지 않을 경우 null
     */
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * Token 유효성 검사 및 예외 처리
     * <p>
     * @param token 전달받은 token
     * @return JWT
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid token", e);
            throw e;
        } catch (ExpiredJwtException e) {
            log.warn("Expired token", e);
            throw e; // 만료된 토큰 재발급이 필요하기 때문에 throw
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }

        return false;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
