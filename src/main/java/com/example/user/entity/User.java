package com.example.user.entity;

import com.example.auth.enums.UserRole;
import com.example.user.dto.request.SignUpRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // 원래는 동명이인 때문에 이름 unique 설정 옳지 않지만 이 번 한해서 식별자로 사용하기 위해 unique 설정
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private String password;

    // 기본 생성자 잠그고 스태틱 팩토리 메서드 생성
    public static User createUser(SignUpRequest request) {
        User user = new User();
        user.username = request.getUsername();
        user.nickname = request.getNickname();
        user.password = request.getPassword();
        user.userRole = request.getUserRole() == null ? UserRole.ROLE_USER : UserRole.of(request.getUserRole()); // userRole 미입력시 기본값으로 USER
        return user;
    }

}
