package com.luxury.hotel.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    // Secret Base64 de 256 bits para pruebas
    private static final String TEST_SECRET =
        "dGVzdFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3Nlc09ubHkxMjM0NTY3ODk=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24h

        userDetails = User.builder()
            .username("admin@hotelluxury.com")
            .password("encodedPassword")
            .authorities(Collections.emptyList())
            .build();
    }

    @Test
    void generateToken_deberiaRetornarTokenNoNulo() {
        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void extractUsername_deberiaDevolverEmailCorrecto() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("admin@hotelluxury.com");
    }

    @Test
    void isTokenValid_tokenValido_deberiaRetornarTrue() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_tokenDeOtroUsuario_deberiaRetornarFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otroUsuario = User.builder()
            .username("otro@hotel.com")
            .password("pass")
            .authorities(Collections.emptyList())
            .build();
        assertThat(jwtService.isTokenValid(token, otroUsuario)).isFalse();
    }

    @Test
    void generateToken_conTokenExpirado_isTokenValidDeberiaLanzarExcepcion() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // expirado
        String tokenExpirado = jwtService.generateToken(userDetails);
        assertThatThrownBy(() -> jwtService.isTokenValid(tokenExpirado, userDetails))
            .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }
}
