package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.response.DisponibilidadResponse;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.service.HorarioValidacionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Módulo 4 – Reglamento: Endpoints de consulta de disponibilidad de horarios.
 *
 * <p>Permite al frontend (u otros módulos) verificar si una cancha o un árbitro
 * están libres en un horario dado, antes de intentar programar un partido.</p>
 *
 * <p>Base URL: {@code /api/reglamento}</p>
 */
@RestController
@RequestMapping("/api/reglamento")
public class ReglamentoController {

    private final HorarioValidacionService horarioValidacionService;

    public ReglamentoController(HorarioValidacionService horarioValidacionService) {
        this.horarioValidacionService = horarioValidacionService;
    }

    // -----------------------------------------------------------------------
    // GET /api/reglamento/disponibilidad/cancha/{canchaId}?inicio=...
    // RN-01: Verifica que la cancha esté libre en el horario solicitado.
    // -----------------------------------------------------------------------

    /**
     * Consulta si una cancha está disponible en el horario indicado.
     *
     * @param canchaId ID de la cancha a consultar.
     * @param inicio   Fecha y hora de inicio a verificar (formato: {@code yyyy-MM-dd'T'HH:mm:ss}).
     * @return 200 OK con {@code disponible: true} si no hay conflicto,
     *         o {@code disponible: false, motivo: "..."} si la cancha está ocupada.
     *
     * Ejemplo de uso:
     * GET /api/reglamento/disponibilidad/cancha/1?inicio=2025-06-14T10:00:00
     */
    @GetMapping("/disponibilidad/cancha/{canchaId}")
    public ResponseEntity<DisponibilidadResponse> consultarDisponibilidadCancha(
            @PathVariable Integer canchaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio) {

        try {
            // Pasamos null como idExcluido porque es una consulta pura, no una edición
            horarioValidacionService.validarDisponibilidadCancha(canchaId, inicio, null);
            return ResponseEntity.ok(new DisponibilidadResponse(true));

        } catch (ConflictException ex) {
            // La cancha tiene un conflicto: lo informamos sin lanzar error HTTP
            return ResponseEntity.ok(new DisponibilidadResponse(false, ex.getMessage()));
        }
    }

    // -----------------------------------------------------------------------
    // GET /api/reglamento/disponibilidad/arbitro/{arbitroId}?inicio=...
    // RN-02: Verifica que el árbitro esté libre en el horario solicitado.
    // -----------------------------------------------------------------------

    /**
     * Consulta si un árbitro está disponible en el horario indicado.
     *
     * @param arbitroId ID del árbitro a consultar.
     * @param inicio    Fecha y hora de inicio a verificar (formato: {@code yyyy-MM-dd'T'HH:mm:ss}).
     * @return 200 OK con {@code disponible: true} si no hay conflicto,
     *         o {@code disponible: false, motivo: "..."} si el árbitro ya tiene partido.
     *
     * Ejemplo de uso:
     * GET /api/reglamento/disponibilidad/arbitro/10?inicio=2025-06-14T10:00:00
     */
    @GetMapping("/disponibilidad/arbitro/{arbitroId}")
    public ResponseEntity<DisponibilidadResponse> consultarDisponibilidadArbitro(
            @PathVariable Integer arbitroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio) {

        try {
            // Pasamos null como idExcluido porque es una consulta pura, no una edición
            horarioValidacionService.validarDisponibilidadArbitro(arbitroId, inicio, null);
            return ResponseEntity.ok(new DisponibilidadResponse(true));

        } catch (ConflictException ex) {
            // El árbitro tiene un conflicto: lo informamos sin lanzar error HTTP
            return ResponseEntity.ok(new DisponibilidadResponse(false, ex.getMessage()));
        }
    }
}
