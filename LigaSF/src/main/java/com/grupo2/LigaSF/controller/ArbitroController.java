package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.request.ArbitroRequest;
import com.grupo2.LigaSF.dto.response.ArbitroResponse;
import com.grupo2.LigaSF.service.ArbitroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arbitros")
public class ArbitroController {

    private final ArbitroService arbitroService;

    public ArbitroController(ArbitroService arbitroService) {
        this.arbitroService = arbitroService;
    }

    @PostMapping
    public ResponseEntity<ArbitroResponse> crear(@Valid @RequestBody ArbitroRequest request) {
        ArbitroResponse creado = arbitroService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<ArbitroResponse>> listar(
            @RequestParam(defaultValue = "true") boolean soloActivos) {
        return ResponseEntity.ok(arbitroService.listar(soloActivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArbitroResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(arbitroService.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ArbitroResponse> desactivar(@PathVariable Integer id) {
        return ResponseEntity.ok(arbitroService.desactivar(id));
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ArbitroResponse> reactivar(@PathVariable Integer id) {
        return ResponseEntity.ok(arbitroService.reactivar(id));
    }
}
