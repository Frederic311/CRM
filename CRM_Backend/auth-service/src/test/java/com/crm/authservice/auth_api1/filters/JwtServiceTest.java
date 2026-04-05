package com.crm.authservice.auth_api1.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        String rawKey = "012345678901234567890123456789012345678901234567";
        String base64Key = Base64.getEncoder().encodeToString(rawKey.getBytes(StandardCharsets.UTF_8));

        ReflectionTestUtils.setField(jwtService, "secretKey", base64Key);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3_600_000L);
    }

    @Test
    void extractUserRole_shouldPreferAdminWhenMultipleAuthoritiesExist() {
        UserDetails user = User.withUsername("admin@example.com")
                .password("pwd")
                .authorities("USER", "ADMIN")
                .build();

        String token = jwtService.generateToken(user);

        assertEquals("ADMIN", jwtService.extractUserRole(token));
    }

    @Test
    void extractUserRole_shouldReturnFirstAuthorityWhenAdminMissing() {
        UserDetails user = User.withUsername("user@example.com")
                .password("pwd")
                .authorities("MANAGER", "USER")
                .build();

        String token = jwtService.generateToken(user);

        assertEquals("MANAGER", jwtService.extractUserRole(token));
    }

    @Test
    void isTokenValid_shouldMatchTokenSubject() {
        UserDetails owner = User.withUsername("owner@example.com")
                .password("pwd")
                .authorities("USER")
                .build();

        UserDetails other = User.withUsername("other@example.com")
                .password("pwd")
                .authorities("USER")
                .build();

        String token = jwtService.generateToken(owner);

        assertTrue(jwtService.isTokenValid(token, owner));
        assertFalse(jwtService.isTokenValid(token, other));
    }
}
