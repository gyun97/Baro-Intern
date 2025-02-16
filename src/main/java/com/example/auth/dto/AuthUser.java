package com.example.auth.dto;

import com.example.auth.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AuthUser {

    private final Long userId;
    private final String userName;
    private final String nickName;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(Long userId, String userName, String nickName, UserRole role) {
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }

}
