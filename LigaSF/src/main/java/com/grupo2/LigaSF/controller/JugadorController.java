package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.request.JugadorRequest;
import com.grupo2.LigaSF.dto.response.JugadorResponse;
import com.grupo2.LigaSF.service.JugadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @PostMapping
    public ResponseEntity<JugadorResponse> crear(@Valid @RequestBody JugadorRequest request) {
        JugadorResponse creado = jugadorService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<JugadorResponse>> listar(
            @RequestParam(required = false) Integer equipoId,
            @RequestParam(defaultValue = "true") boolean soloActivos) {
        List<JugadorResponse> jugadores = jugadorService.listar(equipoId, soloActivos);
        return ResponseEntity.ok(jugadores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JugadorResponse> obtenerPorId(@PathVariable Integer id) {
        JugadorResponse jugador = jugadorService.obtenerPorId(id);
        return ResponseEntity.ok(jugador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JugadorResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody JugadorRequest request) {
        JugadorResponse actualizado = jugadorService.actualizar(id, request);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JugadorResponse> desactivar(@PathVariable Integer id) {
        JugadorResponse desactivado = jugadorService.desactivar(id);
        return ResponseEntity.ok(desactivado);
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<JugadorResponse> reactivar(@PathVariable Integer id) {
        JugadorResponse reactivado = jugadorService.reactivar(id);
        return ResponseEntity.ok(reactivado);
    }
}
