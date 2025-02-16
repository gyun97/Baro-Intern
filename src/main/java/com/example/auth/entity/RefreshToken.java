package com.example.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "tokens")
public class RefreshToken {

    @Id
    @Column(nullable = false, name = "user_id_key")
    private Long key;

    @Column(nullable = false, unique = true, name = "token_value")
    private String value;

    public static RefreshToken generateRefreshToken(Long key, String value) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.key = key;
        refreshToken.value = value;
        return refreshToken;
    }

    public void updateValue(String token) {
        this.value = token;
    }
}
