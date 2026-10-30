package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.request.TorneoRequest;
import com.grupo2.LigaSF.dto.response.TorneoResponse;
import com.grupo2.LigaSF.service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    @PostMapping
    public ResponseEntity<TorneoResponse> crear(@Valid @RequestBody TorneoRequest request) {
        TorneoResponse creado = torneoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<TorneoResponse>> listar(
            @RequestParam(defaultValue = "true") boolean soloActivos) {
        return ResponseEntity.ok(torneoService.listar(soloActivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TorneoResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(torneoService.obtenerPorId(id));
    }
}
