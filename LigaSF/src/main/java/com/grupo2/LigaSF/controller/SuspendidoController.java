package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.response.SuspendidoResponse;
import com.grupo2.LigaSF.service.SuspendidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/torneos/{torneoId}/fechas/{numeroFecha}/suspendidos")
public class SuspendidoController {

    private final SuspendidoService suspendidoService;

    public SuspendidoController(SuspendidoService suspendidoService) {
        this.suspendidoService = suspendidoService;
    }

    @GetMapping
    public ResponseEntity<List<SuspendidoResponse>> listar(@PathVariable Integer torneoId,
                                                           @PathVariable Integer numeroFecha) {
        return ResponseEntity.ok(suspendidoService.listarPorFecha(torneoId, numeroFecha));
    }
}
