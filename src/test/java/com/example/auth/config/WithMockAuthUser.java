package com.example.auth.config;

import com.example.auth.enums.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestSecurityContextFactory.class)
public @interface WithMockAuthUser {
    long userId() default 1L;
    String username() default "user1";
    String nickname() default  "nickname1";
    UserRole role() default UserRole.ROLE_USER;
}