package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.request.CanchaRequest;
import com.grupo2.LigaSF.dto.response.CanchaResponse;
import com.grupo2.LigaSF.service.CanchaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canchas")
public class CanchaController {

    private final CanchaService canchaService;

    public CanchaController(CanchaService canchaService) {
        this.canchaService = canchaService;
    }

    @PostMapping
    public ResponseEntity<CanchaResponse> crear(@Valid @RequestBody CanchaRequest request) {
        CanchaResponse creada = canchaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<CanchaResponse>> listar(
            @RequestParam(defaultValue = "true") boolean soloActivos) {
        return ResponseEntity.ok(canchaService.listar(soloActivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CanchaResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(canchaService.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CanchaResponse> desactivar(@PathVariable Integer id) {
        return ResponseEntity.ok(canchaService.desactivar(id));
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<CanchaResponse> reactivar(@PathVariable Integer id) {
        return ResponseEntity.ok(canchaService.reactivar(id));
    }
}
