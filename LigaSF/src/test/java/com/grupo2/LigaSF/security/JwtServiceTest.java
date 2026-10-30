package com.grupo2.LigaSF.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("9a3b6c7d8e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3d4e5f6a7b", 8);
    }

    @Test
    @DisplayName("Debe generar token válido con username y rol extraíbles")
    void generarYValidarToken() {
        String token = jwtService.generateToken("admin", "ADMIN");

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertEquals("admin", jwtService.extractUsername(token));
        assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    @DisplayName("Debe fallar validación de token corrupto o mal formado")
    void tokenInvalido() {
        assertFalse(jwtService.isTokenValid("token.invalido.falso"));
    }
}
