package com.example.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignInRequest {

    @NotBlank(message = "이름을 입력하세요!")
    private String username;

    @NotBlank(message = "비밀번호를 입력하세요!")
    private String password;
}
