package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.response.DisponibilidadResponse;
import com.grupo2.LigaSF.dto.response.SancionResponse;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.service.HorarioValidacionService;
import com.grupo2.LigaSF.service.SancionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Módulo 4 – Reglamento: Endpoints de consulta de horarios y sanciones.
 *
 * <p>Funcionalidad 1 (RN-01/RN-02): disponibilidad de cancha y árbitro.</p>
 * <p>Funcionalidad 2 (RN-03): consulta de jugadores sancionados por fecha.</p>
 *
 * <p>Base URL: {@code /api/reglamento}</p>
 */
@RestController
@RequestMapping("/api/reglamento")
public class ReglamentoController {

    private final HorarioValidacionService horarioValidacionService;
    private final SancionService sancionService;

    public ReglamentoController(HorarioValidacionService horarioValidacionService,
                                SancionService sancionService) {
        this.horarioValidacionService = horarioValidacionService;
        this.sancionService = sancionService;
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

    // -----------------------------------------------------------------------
    // GET /api/reglamento/sanciones?torneoId=1&fecha=3
    // RN-03: Lista todos los jugadores suspendidos para una fecha del torneo.
    // -----------------------------------------------------------------------

    /**
     * Lista todos los jugadores suspendidos para una fecha específica de un torneo.
     *
     * <p>Un jugador queda suspendido si en la fecha N-1 recibió tarjeta roja o
     * dos amarillas en el mismo partido (RN-03).</p>
     *
     * @param torneoId ID del torneo.
     * @param fecha    Número de la fecha para la cual se consultan las sanciones.
     * @return 200 OK con lista de {@link SancionResponse}. Vacía si no hay suspendidos.
     *
     * Ejemplo: GET /api/reglamento/sanciones?torneoId=1&fecha=3
     */
    @GetMapping("/sanciones")
    public ResponseEntity<List<SancionResponse>> listarSancionados(
            @RequestParam Integer torneoId,
            @RequestParam Integer fecha) {

        List<SancionResponse> sancionados = sancionService.listarSancionados(torneoId, fecha);
        return ResponseEntity.ok(sancionados);
    }

    // -----------------------------------------------------------------------
    // GET /api/reglamento/sanciones/jugador/{jugadorId}?torneoId=1&fecha=3
    // RN-03: Consulta si un jugador específico está suspendido en una fecha.
    // -----------------------------------------------------------------------

    /**
     * Verifica si un jugador específico está suspendido para una fecha.
     *
     * <p>Útil para el módulo de inscripción (Módulo 3) antes de habilitar
     * a un jugador para jugar un partido.</p>
     *
     * @param jugadorId ID del jugador a consultar.
     * @param torneoId  ID del torneo.
     * @param fecha     Número de la fecha a consultar.
     * @return 200 OK con {@code {"suspendido": true/false}}.
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
}
