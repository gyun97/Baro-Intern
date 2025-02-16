package com.example.user.dto.response;

import com.example.auth.enums.UserRole;
import com.example.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignUpResponse {

    private String userName;
    private String nickname;
    private UserRole authorities;

    public static SignUpResponse of(User user) {
        return new SignUpResponse(
                user.getUsername(),
                user.getNickname(),
                user.getUserRole()
        );
    }

}
