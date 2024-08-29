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
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;


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

    // accessToken 발급 (제거 예정)
    public JWTDto generateAccessToken(Member member) {
        Date now = new Date();
        Claims claims = Jwts.claims();
        claims.put("sub", member.getName());
        claims.put("jti", member.getLoginId());

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRED_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return JWTDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    // accessToken + refreshToken 발급
    public JWTDto generateAccessNRefreshToken(Member member) { // ,List<> roles
        Date now = new Date();
        String accessToken = generateAccessToken(member).getAccessToken();

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

    // Jwt 내 모든 클레임 추출
    private Claims getAllClaimsFromJwt(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    // Jwt 내 클레임 추출
    private <T> T getClaimsFromJwt(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromJwt(token);
        return claimsResolver.apply(claims);
    }

    // Jwt 내 사용자 로그인 아이디 추출
    public String getLoginId(String token) {
        return getClaimsFromJwt(token, Claims::getId);
    }

    // UserDetails 활용하여 DB에서 사용자 정보 추출
    public CustomUserDetails getUserDetailsLoginId(String token) {
        String loginId = this.getLoginId(token);
        return memberService.loadUserByUsername(loginId);
    }

    // 추출한 사용자 정보를 인증 객체에 담아줌
    public Authentication getAuthentication(String accessToken) {
        CustomUserDetails principal = getUserDetailsLoginId(accessToken);
        return new UsernamePasswordAuthenticationToken(principal.getUsername(), principal.getPassword(), principal.getAuthorities());
    }

    // header의 token 값 존재 유무 판별
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // token 유효성 검사
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
