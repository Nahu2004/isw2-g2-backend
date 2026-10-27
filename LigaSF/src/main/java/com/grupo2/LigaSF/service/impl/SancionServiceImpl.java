package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.entity.EventoPartido;
import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.entity.enums.TipoEvento;
import com.grupo2.LigaSF.repository.EventoPartidoRepository;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.SancionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SancionServiceImpl implements SancionService {

    private final PartidoRepository partidoRepository;
    private final EventoPartidoRepository eventoPartidoRepository;

    public SancionServiceImpl(PartidoRepository partidoRepository,
                             EventoPartidoRepository eventoPartidoRepository) {
        this.partidoRepository = partidoRepository;
        this.eventoPartidoRepository = eventoPartidoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaJugadorSuspendido(Integer jugadorId, Integer torneoId, Integer numeroFecha) {
        if (numeroFecha == null || numeroFecha <= 1) {
            return false;
        }
        return obtenerJugadoresSuspendidos(torneoId, numeroFecha).contains(jugadorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Integer> obtenerJugadoresSuspendidos(Integer torneoId, Integer numeroFecha) {
        if (numeroFecha == null || numeroFecha <= 1) {
            return Collections.emptySet();
        }

        int fechaPrevia = numeroFecha - 1;
        List<Partido> partidosPrevios = partidoRepository.findByTorneoIdAndNumeroFecha(torneoId, fechaPrevia);

        Set<Integer> suspendidos = new HashSet<>();

        for (Partido p : partidosPrevios) {
            // Solo partidos FINALIZADOS de la fecha N generan suspensión para la fecha N+1 (RN-03)
            if (p.getEstado() == EstadoPartido.FINALIZADO) {
                List<EventoPartido> eventos = eventoPartidoRepository.findByPartidoId(p.getId());

                Map<Integer, Integer> amarillasPorJugador = new HashMap<>();

                for (EventoPartido evento : eventos) {
                    Integer jId = evento.getJugador().getId();

                    if (evento.getTipo() == TipoEvento.ROJA) {
                        suspendidos.add(jId);
                    } else if (evento.getTipo() == TipoEvento.AMARILLA) {
                        int conteo = amarillasPorJugador.getOrDefault(jId, 0) + 1;
                        amarillasPorJugador.put(jId, conteo);
                        if (conteo >= 2) {
                            suspendidos.add(jId);
                        }
                    }
                }
            }
        }

        return suspendidos;
    }
}
