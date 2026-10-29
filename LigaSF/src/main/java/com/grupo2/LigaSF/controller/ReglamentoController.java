package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.response.DisponibilidadResponse;
import com.grupo2.LigaSF.dto.response.SancionResponse;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.service.BloqueoJugadorService;
import com.grupo2.LigaSF.service.HorarioValidacionService;
import com.grupo2.LigaSF.service.SancionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Módulo 4 – Reglamento: Endpoints de consulta de horarios, sanciones y bloqueos.
 *
 * <p>Funcionalidad 1 (RN-01/RN-02): disponibilidad de cancha y árbitro.</p>
 * <p>Funcionalidad 2 (RN-03): consulta de jugadores sancionados por fecha.</p>
 * <p>Funcionalidad 3 (RN-03/RN-04): verificación de habilitación de jugadores.</p>
 *
 * <p>Base URL: {@code /api/reglamento}</p>
 */
@RestController
@RequestMapping("/api/reglamento")
public class ReglamentoController {

    private final HorarioValidacionService horarioValidacionService;
    private final SancionService sancionService;
    private final BloqueoJugadorService bloqueoJugadorService;

    public ReglamentoController(HorarioValidacionService horarioValidacionService,
                                SancionService sancionService,
                                BloqueoJugadorService bloqueoJugadorService) {
        this.horarioValidacionService = horarioValidacionService;
        this.sancionService = sancionService;
        this.bloqueoJugadorService = bloqueoJugadorService;
    }

    // =========================================================================
    // FUNCIONALIDAD 1: Validación de horarios (RN-01 / RN-02)
    // =========================================================================

    /**
     * Consulta si una cancha está disponible en el horario indicado (RN-01).
     *
     * Ejemplo: GET /api/reglamento/disponibilidad/cancha/1?inicio=2025-06-14T10:00:00
     */
    @GetMapping("/disponibilidad/cancha/{canchaId}")
    public ResponseEntity<DisponibilidadResponse> consultarDisponibilidadCancha(
            @PathVariable Integer canchaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio) {

        try {
            horarioValidacionService.validarDisponibilidadCancha(canchaId, inicio, null);
            return ResponseEntity.ok(new DisponibilidadResponse(true));
        } catch (ConflictException ex) {
            return ResponseEntity.ok(new DisponibilidadResponse(false, ex.getMessage()));
        }
    }

    /**
     * Consulta si un árbitro está disponible en el horario indicado (RN-02).
     *
     * Ejemplo: GET /api/reglamento/disponibilidad/arbitro/10?inicio=2025-06-14T10:00:00
     */
    @GetMapping("/disponibilidad/arbitro/{arbitroId}")
    public ResponseEntity<DisponibilidadResponse> consultarDisponibilidadArbitro(
            @PathVariable Integer arbitroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio) {

        try {
            horarioValidacionService.validarDisponibilidadArbitro(arbitroId, inicio, null);
            return ResponseEntity.ok(new DisponibilidadResponse(true));
        } catch (ConflictException ex) {
            return ResponseEntity.ok(new DisponibilidadResponse(false, ex.getMessage()));
        }
    }

    // =========================================================================
    // FUNCIONALIDAD 2: Sanciones (RN-03)
    // =========================================================================

    /**
     * Lista todos los jugadores suspendidos para una fecha específica del torneo (RN-03).
     *
     * Ejemplo: GET /api/reglamento/sanciones?torneoId=1&fecha=3
     */
    @GetMapping("/sanciones")
    public ResponseEntity<List<SancionResponse>> listarSancionados(
            @RequestParam Integer torneoId,
            @RequestParam Integer fecha) {

        return ResponseEntity.ok(sancionService.listarSancionados(torneoId, fecha));
    }

    /**
     * Verifica si un jugador específico está suspendido para una fecha (RN-03).
     *
     * Ejemplo: GET /api/reglamento/sanciones/jugador/5?torneoId=1&fecha=3
     */
    @GetMapping("/sanciones/jugador/{jugadorId}")
    public ResponseEntity<Map<String, Boolean>> consultarSuspensionJugador(
            @PathVariable Integer jugadorId,
            @RequestParam Integer torneoId,
            @RequestParam Integer fecha) {

        boolean suspendido = sancionService.estaJugadorSuspendido(jugadorId, torneoId, fecha);
        return ResponseEntity.ok(Map.of("suspendido", suspendido));
    }

    // =========================================================================
    // FUNCIONALIDAD 3: Bloqueo de jugadores (RN-03 + RN-04)
    // =========================================================================

    /**
     * Verifica si un jugador está habilitado para jugar una fecha del torneo.
     *
     * <p>Un jugador está bloqueado si está suspendido por tarjetas (RN-03)
     * o si está desactivado en el sistema (baja lógica).</p>
     *
     * <p>Este endpoint es el que consume el frontend antes de mostrar
     * a un jugador como disponible para inscribirse en un partido.</p>
     *
     * @return {@code {"bloqueado": false}} si puede jugar.
     *         {@code {"bloqueado": true, "motivo": "..."}} si no puede.
     *
     * Ejemplo: GET /api/reglamento/bloqueo/jugador/5?torneoId=1&fecha=3
     */
    @GetMapping("/bloqueo/jugador/{jugadorId}")
    public ResponseEntity<Map<String, Object>> consultarBloqueoJugador(
            @PathVariable Integer jugadorId,
            @RequestParam Integer torneoId,
            @RequestParam Integer fecha) {

        boolean bloqueado = bloqueoJugadorService.estaJugadorBloqueado(jugadorId, torneoId, fecha);

        if (bloqueado) {
            // Determinamos el motivo específico para informar al usuario
            String motivo = sancionService.estaJugadorSuspendido(jugadorId, torneoId, fecha)
                    ? "El jugador está suspendido por tarjetas para esta fecha (RN-03)."
                    : "El jugador está desactivado en el sistema.";
            return ResponseEntity.ok(Map.of("bloqueado", true, "motivo", motivo));
        }

        return ResponseEntity.ok(Map.of("bloqueado", false));
    }
}
