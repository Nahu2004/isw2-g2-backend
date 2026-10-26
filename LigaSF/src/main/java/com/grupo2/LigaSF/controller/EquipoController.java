package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.request.EquipoRequest;
import com.grupo2.LigaSF.dto.response.EquipoResponse;
import com.grupo2.LigaSF.service.EquipoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @PostMapping
    public ResponseEntity<EquipoResponse> crear(@Valid @RequestBody EquipoRequest request) {
        EquipoResponse creado = equipoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<EquipoResponse>> listar(
            @RequestParam(required = false) Integer torneoId,
            @RequestParam(defaultValue = "true") boolean soloActivos) {
        List<EquipoResponse> equipos = equipoService.listar(torneoId, soloActivos);
        return ResponseEntity.ok(equipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoResponse> obtenerPorId(@PathVariable Integer id) {
        EquipoResponse equipo = equipoService.obtenerPorId(id);
        return ResponseEntity.ok(equipo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EquipoRequest request) {
        EquipoResponse actualizado = equipoService.actualizar(id, request);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EquipoResponse> desactivar(@PathVariable Integer id) {
        EquipoResponse desactivado = equipoService.desactivar(id);
        return ResponseEntity.ok(desactivado);
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<EquipoResponse> reactivar(@PathVariable Integer id) {
        EquipoResponse reactivado = equipoService.reactivar(id);
        return ResponseEntity.ok(reactivado);
    }
}
