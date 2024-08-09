package com.project.mokkozi.auth;

import com.project.mokkozi.model.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@RequiredArgsConstructor
@Slf4j
@Component
public class JWTProvider {

    private final Key secretKey;
    private final long tokenExpiredTime = 1000L * 60 * 60;

    @Autowired
    public JWTProvider(@Value("${jwt.secret.key}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Member member) { // ,List<> roles
        Date now = new Date();
        Claims claims = Jwts.claims();
        claims.put("sub", member.getName());
        claims.put("jti", member.getLoginId());

        Map<String, Object> headers = new HashMap<>() {{
            put("typ", "JWT");
            put("alg", "HS256");
        }};

        String token = Jwts.builder()
                .setHeader(headers)
                .setSubject(member.getName())
                .setId(member.getLoginId())
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + tokenExpiredTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return token;
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

    // Jwt 내 만료 날짜 추출
    public Date getExpirationDateFromJwt(String token) {
        return getClaimsFromJwt(token, Claims::getExpiration);
    }

    // Jwt 내 사용자 이름 추출
    public String getMemberName(String token) {
        return getClaimsFromJwt(token, Claims::getSubject);
    }

    // Jwt 토큰의 만료 여부 체크
    private boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromJwt(token);
        return expirationDate.after(new Date());
    }

}
