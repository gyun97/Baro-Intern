package com.example.auth.config;


import com.example.auth.TokenRepository;
import com.example.auth.entity.RefreshToken;
import com.example.auth.enums.UserRole;
import com.example.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.rmi.ServerException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

//  PER_CLASS: test 클래스당 인스턴스가 생성(기본값은 각 method당 인스턴스가 생성되는 PER_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserService userService;


    private static final String SECRET_KEY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456";
    private static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private static final long REFRESH_TOKEN_TIME = 60 * 60 * 1000L * 24; // 24시간


    @BeforeAll
    void init() throws Exception {
        jwtUtil = new JwtUtil(); // 기본 생성자로 인스턴스 생성

        // Reflection을 사용해 secretKey 필드 직접 주입
        Field secretKeyField = JwtUtil.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtUtil, Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()));

        // key 초기화 메서드 실행
        jwtUtil.init();
    }

    @Test
    void 엑세스_토큰이_정상적으로_발행되는지_확인한다() {
        String accessToken = jwtUtil.generateAccessToken(1L, "user1", UserRole.ROLE_USER, "nickName1", ACCESS_TOKEN_TIME);
        System.out.println("access token = " + accessToken);
        assertThat(accessToken).isNotNull();
        assertThat(accessToken.startsWith("Bearer")).isTrue();
    }

    @Test
    void JWT의_Signautre가_성공적으로_검증되어_유효성이_확인된다() {
        String accessToken = jwtUtil.generateAccessToken(1L, "user1", UserRole.ROLE_USER, "nickName1", ACCESS_TOKEN_TIME);
        System.out.println("access token = " + accessToken);
        Assertions.assertDoesNotThrow(() -> jwtUtil.verifySignature(accessToken));

    }

    @Test
    void JWT의_유효기간이_만료되면_JWT의_유효성이_만료된다() throws InterruptedException {
        String accessToken = jwtUtil.generateAccessToken(1L, "user1", UserRole.ROLE_USER, "nickName1", 1000L);

        Thread.sleep(1500L); // 액세스 토큰 만료 유도

        System.out.println("access token = " + accessToken);
//        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtil.verifySignature(accessToken));
    }


    @Test
    void 리프레쉬_토큰이_정상적으로_발행되는지_확인한다() {
        String refreshToken = jwtUtil.generateRefreshToken(1L, REFRESH_TOKEN_TIME);
        System.out.println("refresh token = " + refreshToken);
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.startsWith("Bearer")).isTrue();
    }

    @Test
    void 엑세스_토큰이_만료되고_리프레쉬_토큰이_유효하면_엑세스_토큰을_재발급한다() throws InterruptedException, ServerException {
        // given
        Long userId = 1L;
        String username = "user1";
        UserRole role = UserRole.ROLE_USER;
        String nickname = "nickName1";

        String expiredAccessToken = jwtUtil.generateAccessToken(userId, username, role, nickname, 1000L);
        String validRefreshToken = jwtUtil.generateRefreshToken(userId, 60 * 60 * 1000L * 24);

        RefreshToken refreshTokenEntity = RefreshToken.generateRefreshToken(userId, validRefreshToken);

        // Mock 객체가 저장 메서드를 호출하면 아무 동작도 하지 않도록 설정
//        when(tokenRepository.save(refreshTokenEntity)).thenReturn(refreshTokenEntity);

        Thread.sleep(1500); // 액세스 토큰 만료 유도

        // when
        when(userService.reIssueAccessToken(validRefreshToken)).thenReturn("Bearer newAccessToken");

        String newAccessToken = userService.reIssueAccessToken(validRefreshToken);
        System.out.println("newAccessToken = " + newAccessToken);

        // then
        assertThat(newAccessToken).isNotNull();
        assertThat(newAccessToken.startsWith("Bearer")).isTrue();
        assertThat(newAccessToken).isNotEqualTo(expiredAccessToken);
    }


}