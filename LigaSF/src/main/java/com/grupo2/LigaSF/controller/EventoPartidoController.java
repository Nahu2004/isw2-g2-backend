package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.request.EventoPartidoRequest;
import com.grupo2.LigaSF.dto.response.EventoPartidoResponse;
import com.grupo2.LigaSF.service.EventoPartidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventoPartidoController {

    private final EventoPartidoService eventoPartidoService;

    public EventoPartidoController(EventoPartidoService eventoPartidoService) {
        this.eventoPartidoService = eventoPartidoService;
    }

    @PostMapping("/partidos/{id}/eventos")
    @PreAuthorize("hasAnyRole('DELEGADO', 'ADMIN')")
    public ResponseEntity<EventoPartidoResponse> registrarEvento(
            @PathVariable Integer id,
            @Valid @RequestBody EventoPartidoRequest request) {
        EventoPartidoResponse response = eventoPartidoService.registrarEvento(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/eventos/{id}")
    @PreAuthorize("hasAnyRole('DELEGADO', 'ADMIN')")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Integer id) {
        eventoPartidoService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partidos/{id}/eventos")
    public ResponseEntity<List<EventoPartidoResponse>> listarEventos(@PathVariable Integer id) {
        List<EventoPartidoResponse> eventos = eventoPartidoService.listarPorPartido(id);
        return ResponseEntity.ok(eventos);
    }
}
