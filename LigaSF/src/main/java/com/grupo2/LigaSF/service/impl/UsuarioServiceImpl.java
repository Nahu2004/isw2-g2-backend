package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.ActualizarUsuarioRequest;
import com.grupo2.LigaSF.dto.request.UsuarioRequest;
import com.grupo2.LigaSF.dto.response.UsuarioResponse;
import com.grupo2.LigaSF.entity.Usuario;
import com.grupo2.LigaSF.entity.enums.RolUsuario;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.exception.UnauthorizedException;
import com.grupo2.LigaSF.repository.UsuarioRepository;
import com.grupo2.LigaSF.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("El username '" + request.getUsername() + "' ya está en uso");
        }

        RolUsuario rol;
        try {
            rol = RolUsuario.valueOf(request.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConflictException("Rol inválido: " + request.getRol() + ". Debe ser ADMIN o DELEGADO");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername().trim().toLowerCase());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreCompleto(request.getNombreCompleto().trim());
        usuario.setRol(rol);
        usuario.setActivo(true);

        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponse actualizar(Integer id, ActualizarUsuarioRequest request, String usernameAdmin) {
        Usuario usuario = buscarUsuario(id);

        // RN-08: admin no puede quitarse su propio rol ADMIN
        if (usuario.getUsername().equals(usernameAdmin) &&
            request.getRol() != null &&
            !request.getRol().equalsIgnoreCase("ADMIN") &&
            usuario.getRol() == RolUsuario.ADMIN) {
            throw new UnauthorizedException("No puedes quitarte tu propio rol de administrador");
        }

        if (request.getNombreCompleto() != null && !request.getNombreCompleto().isBlank()) {
            usuario.setNombreCompleto(request.getNombreCompleto().trim());
        }

        if (request.getRol() != null && !request.getRol().isBlank()) {
            try {
                usuario.setRol(RolUsuario.valueOf(request.getRol().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ConflictException("Rol inválido: " + request.getRol());
            }
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponse desactivar(Integer id, String usernameAdmin) {
        Usuario usuario = buscarUsuario(id);

        // RN-08: admin no puede desactivarse a sí mismo
        if (usuario.getUsername().equals(usernameAdmin)) {
            throw new UnauthorizedException("No puedes desactivar tu propia cuenta");
        }

        usuario.setActivo(false);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponse reactivar(Integer id) {
        Usuario usuario = buscarUsuario(id);
        usuario.setActivo(true);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Integer id) {
        return toResponse(buscarUsuario(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Usuario buscarUsuario(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getUsername(),
                u.getNombreCompleto(),
                u.getRol().name(),
                u.getActivo(),
                u.getFechaAlta()
        );
    }
}
