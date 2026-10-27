package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.request.CambiarEstadoPartidoRequest;
import com.grupo2.LigaSF.dto.response.PartidoResponse;
import com.grupo2.LigaSF.service.PartidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('DELEGADO', 'ADMIN')")
    public ResponseEntity<PartidoResponse> cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody CambiarEstadoPartidoRequest request) {
        PartidoResponse response = partidoService.cambiarEstado(id, request);
        return ResponseEntity.ok(response);
    }
}
