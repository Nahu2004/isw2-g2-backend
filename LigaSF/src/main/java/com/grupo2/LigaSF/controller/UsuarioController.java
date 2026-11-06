package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.request.ActualizarUsuarioRequest;
import com.grupo2.LigaSF.dto.request.UsuarioRequest;
import com.grupo2.LigaSF.dto.response.UsuarioResponse;
import com.grupo2.LigaSF.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarUsuarioRequest request,
            Authentication auth) {
        String usernameAdmin = auth.getName();
        return ResponseEntity.ok(usuarioService.actualizar(id, request, usernameAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> desactivar(
            @PathVariable Integer id,
            Authentication auth) {
        String usernameAdmin = auth.getName();
        return ResponseEntity.ok(usuarioService.desactivar(id, usernameAdmin));
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<UsuarioResponse> reactivar(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.reactivar(id));
    }
}
