package com.example.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignInResponse {
    private String accessToken;
    private String refreshToken;

    public static SignInResponse of(String accessToken, String refreshToken) {
        return new SignInResponse(
                accessToken,
                refreshToken
        );
    }

}
