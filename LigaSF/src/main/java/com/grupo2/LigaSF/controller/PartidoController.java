package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.request.PartidoRequest;
import com.grupo2.LigaSF.dto.response.PartidoResponse;
import com.grupo2.LigaSF.service.PartidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    @PostMapping
    public ResponseEntity<PartidoResponse> programar(@Valid @RequestBody PartidoRequest request) {
        PartidoResponse creado = partidoService.programar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<PartidoResponse>> listar(
            @RequestParam(required = false) Integer torneoId,
            @RequestParam(required = false) Integer numeroFecha) {
        List<PartidoResponse> partidos = partidoService.listar(torneoId, numeroFecha);
        return ResponseEntity.ok(partidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidoResponse> obtenerPorId(@PathVariable Integer id) {
        PartidoResponse partido = partidoService.obtenerPorId(id);
        return ResponseEntity.ok(partido);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartidoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PartidoRequest request) {
        PartidoResponse actualizado = partidoService.actualizar(id, request);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        partidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
