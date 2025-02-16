package com.example.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "등록할 이름을 입력하세요!")
    private String username;

    @NotBlank(message = "등록할 닉네임을 입력하세요!")
    private String nickname;

    @NotBlank(message = "등록한 비밀번호를 입력하세요!")
    private String password;

    private String userRole;

    public SignUpRequest(String username, String nickname, String password) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
    }
}
