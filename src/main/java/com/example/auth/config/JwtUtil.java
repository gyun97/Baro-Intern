package com.example.auth.config;

import com.example.auth.TokenRepository;
import com.example.auth.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.rmi.ServerException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 60분
    public static final long REFRESH_TOKEN_TIME = 60 * 60 * 1000L * 24 * 7; // 7일
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private TokenRepository tokenRepository;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }


    // 엑세스 토큰 생성
    public String generateAccessToken(Long userId, String username, UserRole userRole, String nickname, long expire) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .claim("username", username)
                        .claim("nickname", nickname)
                        .claim("userRole", userRole)
                        .setExpiration(new Date(date.getTime() + expire))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // 리프레쉬 토큰 생성
    public String generateRefreshToken(Long userId, long expire) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
//                        .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
                        .setExpiration(new Date(date.getTime() + expire))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public void verifySignature(String token) throws ServerException {
        String extractToken = substringToken(token); // "Bearer " 제외
        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(extractToken);
    }

    // 인증 타입(Bearer ) 제외한 토큰 추출
    public String substringToken(String tokenValue) throws ServerException {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new ServerException("Token not found");
    }

    // 토큰에서 Claims(유저 정보) 추출
    public Claims extractClaims(String token) throws ServerException {
        String extractToken = substringToken(token); // "Bearer " 제외

        return Jwts.parserBuilder()
                .setSigningKey(key) // Signature에 사용될 Secret Key 설정
                .build()
                .parseClaimsJws(extractToken) // JWT를 파싱해서 Claims를 얻음
                .getBody();
    }

    // 토큰이 만료되었는지 확인
    public boolean isTokenExpired(String token) throws ServerException {
        try {
            Claims claims = extractClaims(token);
            log.info("Refresh Token Expiration Time: {}", claims.getExpiration());
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }

    }




}
