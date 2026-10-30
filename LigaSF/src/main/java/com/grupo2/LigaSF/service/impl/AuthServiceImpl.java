package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.LoginRequest;
import com.grupo2.LigaSF.dto.response.LoginResponse;
import com.grupo2.LigaSF.entity.Usuario;
import com.grupo2.LigaSF.exception.UnauthorizedException;
import com.grupo2.LigaSF.repository.UsuarioRepository;
import com.grupo2.LigaSF.security.JwtService;
import com.grupo2.LigaSF.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        String role = usuario.getRol().name();
        String token = jwtService.generateToken(usuario.getUsername(), role);

        return new LoginResponse(
                token,
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                role
        );
    }
}
