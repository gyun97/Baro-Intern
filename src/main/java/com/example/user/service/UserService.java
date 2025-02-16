package com.example.user.service;

import com.example.auth.TokenRepository;
import com.example.auth.config.JwtUtil;
import com.example.auth.entity.RefreshToken;
import com.example.user.dto.request.SignInRequest;
import com.example.user.dto.request.SignUpRequest;
import com.example.user.dto.response.SignInResponse;
import com.example.user.dto.response.SignUpResponse;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.ServerException;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    private static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private static final long REFRESH_TOKEN_TIME = 60 * 60 * 1000L * 24; // 24시간

    // 유저 회원가입
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {

        // 패스워드 암호화
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User newUser = User.createUser(request);
        User savedUser = userRepository.save(newUser);

        return SignUpResponse.of(savedUser);
    }

    // 유저 로그인
    @Transactional
    public SignInResponse signIn(SignInRequest request) {
        User user = getUser(request.getUsername());

        // 비밀번호 맞는지 체크
        if (!validatePassword(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Not correct password!");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getUserRole(), user.getNickname(), ACCESS_TOKEN_TIME);
//        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getUserRole(), user.getNickname(), 1000L);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), REFRESH_TOKEN_TIME);
        RefreshToken savedRefreshToken1 = RefreshToken.generateRefreshToken(user.getId(), refreshToken);

        tokenRepository.save(savedRefreshToken1);

        return SignInResponse.of(accessToken, refreshToken);
    }

    public String reIssueAccessToken(String token) throws ServerException {
        Claims claims = jwtUtil.extractClaims(token);
        Long userId = Long.valueOf(claims.getSubject());
        RefreshToken refreshToken = tokenRepository.findByKey(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found refresh-token"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found user"));

        // 리프레시 토큰 일치하지 않으면
        if (!refreshToken.getValue().equals(token)) {
            throw new IllegalArgumentException("Not correct token");
        }

        // 리프레시 토큰 유효기간도 끝났으면
        if (jwtUtil.isTokenExpired(token)) {
            throw new IllegalArgumentException("Expired refresh-token");
        }

        // 리프레쉬 토큰도 갱신
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, REFRESH_TOKEN_TIME);
        RefreshToken.generateRefreshToken(userId, newRefreshToken);

        // 새 엑세스 토큰 발급 후 반환
        return jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getUserRole(), user.getNickname(), ACCESS_TOKEN_TIME);
    }

    // Spring Security 작동하는지 테스트
    public String authTest() {
        return "Ok";
    }


    // 로그인 비밀번호 검증
    private boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 유저 가져오기
    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
    }


}
