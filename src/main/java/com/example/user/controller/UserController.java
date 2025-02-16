package com.example.user.controller;


import com.example.user.dto.request.SignInRequest;
import com.example.user.dto.request.SignUpRequest;
import com.example.user.dto.response.SignInResponse;
import com.example.user.dto.response.SignUpResponse;
import com.example.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User API", description = "사용자 관련 API 모음입니다.")
public class UserController {

    private final UserService userService;

    /**
     * 유저 회원가입 API
     *
     * @param signUpRequest
     * @return 회원가입된 유저 정보(유저 이름, 유저 닉네임, 유저 권한)
     */
    @Operation(summary = "유저 회원가입", description = "서비스에 회원가입합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        SignUpResponse response = userService.signUp(signUpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 유저 로그인 API
     *
     * @param signInRequest
     * @return 발급된 Access Token 값
     */
    @Operation(summary = "유저 로그인", description = "서비스에 로그인 합니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@RequestBody @Valid SignInRequest signInRequest) {
        SignInResponse response = userService.signIn(signInRequest);
        return ResponseEntity.ok(response);
    }


    /**
     * Access Token 만료 시 Refresh Token으로 Acess Token 재발급
     *
     * @param refreshToken
     * @return 재발급된 Access Token 값
     * @throws ServerException
     */
    @Operation(summary = "Access Token 재발급", description = "Access Token이 만료되었을 시 Refresh Token으로 Access Token을 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<String> reIssueAccessToken(@RequestParam(name = "refreshToken") String refreshToken) throws ServerException {
        return ResponseEntity.ok(userService.reIssueAccessToken(refreshToken));
    }

    /**
     * Spring Security 인증/인가 확인 테스트 메서드
     *
     * @return OK 확인 표시
     */
    @Operation(summary = "Test 메서드", description = "Spring Security와 JWT가 잘 작동하는지 확인하는 테스트 메서드입니다.")
    @GetMapping("/test")
    public ResponseEntity<String> authTest() {

        return ResponseEntity.ok(userService.authTest());
    }
}
