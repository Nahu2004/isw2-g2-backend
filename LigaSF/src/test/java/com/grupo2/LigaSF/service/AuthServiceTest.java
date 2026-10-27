package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.LoginRequest;
import com.grupo2.LigaSF.dto.response.LoginResponse;
import com.grupo2.LigaSF.entity.Usuario;
import com.grupo2.LigaSF.entity.enums.RolUsuario;
import com.grupo2.LigaSF.exception.UnauthorizedException;
import com.grupo2.LigaSF.repository.UsuarioRepository;
import com.grupo2.LigaSF.security.JwtService;
import com.grupo2.LigaSF.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("delegado1", "$2a$10$hashedpass", "Carlos Delegado", RolUsuario.DELEGADO, true);
        usuario.setId(1);
    }

    @Test
    @DisplayName("Debe loguear exitosamente con credenciales válidas y retornar JWT")
    void loginExitoso() {
        LoginRequest request = new LoginRequest("delegado1", "Password123!");

        when(usuarioRepository.findByUsername("delegado1")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Password123!", usuario.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken("delegado1", "DELEGADO")).thenReturn("mocked-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals("delegado1", response.getUsername());
        assertEquals("DELEGADO", response.getRol());
        assertEquals("Carlos Delegado", response.getNombreCompleto());
    }

    @Test
    @DisplayName("Debe lanzar UnauthorizedException si el usuario no existe (401)")
    void loginUsuarioNoExiste() {
        LoginRequest request = new LoginRequest("inexistente", "Password123!");

        when(usuarioRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar UnauthorizedException si la contraseña es incorrecta (401)")
    void loginPasswordIncorrecta() {
        LoginRequest request = new LoginRequest("delegado1", "WrongPassword");

        when(usuarioRepository.findByUsername("delegado1")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("WrongPassword", usuario.getPasswordHash())).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar UnauthorizedException si el usuario se encuentra inactivo (401)")
    void loginUsuarioInactivo() {
        usuario.setActivo(false);
        LoginRequest request = new LoginRequest("delegado1", "Password123!");

        when(usuarioRepository.findByUsername("delegado1")).thenReturn(Optional.of(usuario));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.login(request));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }
}
