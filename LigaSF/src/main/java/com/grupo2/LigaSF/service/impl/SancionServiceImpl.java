package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.response.SancionResponse;
import com.grupo2.LigaSF.entity.EventoPartido;
import com.grupo2.LigaSF.entity.Jugador;
import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.entity.enums.TipoEvento;
import com.grupo2.LigaSF.repository.EventoPartidoRepository;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.SancionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Módulo 4 – Reglamento: Implementación del cálculo de sanciones.
 *
 * <p>Implementa la regla RN-03:</p>
 * <ul>
 *   <li>Si en un partido FINALIZADO de la fecha N un jugador recibe <b>1 tarjeta roja</b>,
 *       queda suspendido para la fecha N+1.</li>
 *   <li>Si en un partido FINALIZADO de la fecha N un jugador recibe <b>2 amarillas en el
 *       mismo partido</b>, queda suspendido para la fecha N+1.</li>
 *   <li>Las amarillas de <b>partidos distintos NO se acumulan</b> entre sí.</li>
 * </ul>
 *
 * <p>Lee los eventos del Módulo 2 ({@code EventoPartido}) para calcular las sanciones.</p>
 */
@Service
public class SancionServiceImpl implements SancionService {

    private final PartidoRepository partidoRepository;
    private final EventoPartidoRepository eventoPartidoRepository;

    public SancionServiceImpl(PartidoRepository partidoRepository,
                              EventoPartidoRepository eventoPartidoRepository) {
        this.partidoRepository = partidoRepository;
        this.eventoPartidoRepository = eventoPartidoRepository;
    }

    // -------------------------------------------------------------------------
    // estaJugadorSuspendido — validación rápida para un jugador específico
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public boolean estaJugadorSuspendido(Integer jugadorId, Integer torneoId, Integer numeroFecha) {
        if (numeroFecha == null || numeroFecha <= 1) {
            return false;
        }
        return obtenerJugadoresSuspendidos(torneoId, numeroFecha).contains(jugadorId);
    }

    // -------------------------------------------------------------------------
    // obtenerJugadoresSuspendidos — devuelve solo IDs (validaciones internas)
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Set<Integer> obtenerJugadoresSuspendidos(Integer torneoId, Integer numeroFecha) {
        if (numeroFecha == null || numeroFecha <= 1) {
            return Collections.emptySet();
        }

        Set<Integer> suspendidos = new HashSet<>();

        // Recorremos los registros de sanción y extraemos solo los IDs
        for (SancionResponse sancion : listarSancionados(torneoId, numeroFecha)) {
            suspendidos.add(sancion.getJugadorId());
        }

        return suspendidos;
    }

    // -------------------------------------------------------------------------
    // listarSancionados — RN-03 con detalle completo (Módulo 4 - Reglamento)
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<SancionResponse> listarSancionados(Integer torneoId, Integer numeroFecha) {
        if (numeroFecha == null || numeroFecha <= 1) {
            return Collections.emptyList();
        }

        int fechaPrevia = numeroFecha - 1;

        // Traemos los partidos FINALIZADOS de la fecha N-1 (los únicos que generan sanción)
        List<Partido> partidosPrevios =
                partidoRepository.findByTorneoIdAndNumeroFecha(torneoId, fechaPrevia);

        List<SancionResponse> sanciones = new ArrayList<>();

        for (Partido partido : partidosPrevios) {
            // RN-03: solo los partidos FINALIZADOS generan suspensión
            if (partido.getEstado() != EstadoPartido.FINALIZADO) {
                continue;
            }

            List<EventoPartido> eventos = eventoPartidoRepository.findByPartidoId(partido.getId());

            // Contamos amarillas POR JUGADOR dentro de este partido específico
            // (las amarillas de partidos distintos NO se acumulan — RN-03)
            Map<Integer, Integer> amarillasPorJugador = new HashMap<>();

            for (EventoPartido evento : eventos) {
                Jugador jugador = evento.getJugador();
                Integer jugadorId = jugador.getId();
                String nombreCompleto = jugador.getNombre() + " " + jugador.getApellido();

                if (evento.getTipo() == TipoEvento.ROJA) {
                    // Tarjeta roja: suspensión directa
                    sanciones.add(new SancionResponse(
                            jugadorId,
                            nombreCompleto,
                            numeroFecha,
                            "ROJA"
                    ));

                } else if (evento.getTipo() == TipoEvento.AMARILLA) {
                    // Contamos las amarillas acumuladas EN ESTE PARTIDO
                    int acumuladas = amarillasPorJugador.getOrDefault(jugadorId, 0) + 1;
                    amarillasPorJugador.put(jugadorId, acumuladas);

                    if (acumuladas == 2) {
                        // Segunda amarilla en el mismo partido: suspensión
                        sanciones.add(new SancionResponse(
                                jugadorId,
                                nombreCompleto,
                                numeroFecha,
                                "DOS_AMARILLAS"
                        ));
                    }
                    // Si acumuladas > 2: ya fue contado al llegar a 2, no duplicamos
                }
            }
        }

        return sanciones;
    }
}
