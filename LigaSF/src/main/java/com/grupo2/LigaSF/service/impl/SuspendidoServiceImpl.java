package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.response.SuspendidoResponse;
import com.grupo2.LigaSF.entity.EventoPartido;
import com.grupo2.LigaSF.entity.Jugador;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.entity.enums.TipoEvento;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.EventoPartidoRepository;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.repository.TorneoRepository;
import com.grupo2.LigaSF.service.SancionService;
import com.grupo2.LigaSF.service.SuspendidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SuspendidoServiceImpl implements SuspendidoService {

    private static final String MOTIVO_ROJA = "ROJA";
    private static final String MOTIVO_DOBLE_AMARILLA = "DOBLE_AMARILLA";

    private final SancionService sancionService;
    private final TorneoRepository torneoRepository;
    private final PartidoRepository partidoRepository;
    private final EventoPartidoRepository eventoPartidoRepository;

    public SuspendidoServiceImpl(SancionService sancionService,
                                 TorneoRepository torneoRepository,
                                 PartidoRepository partidoRepository,
                                 EventoPartidoRepository eventoPartidoRepository) {
        this.sancionService = sancionService;
        this.torneoRepository = torneoRepository;
        this.partidoRepository = partidoRepository;
        this.eventoPartidoRepository = eventoPartidoRepository;
    }

    @Override
    public List<SuspendidoResponse> listarPorFecha(Integer torneoId, Integer numeroFecha) {
        if (!torneoRepository.existsById(torneoId)) {
            throw new ResourceNotFoundException("Torneo no encontrado con id: " + torneoId);
        }

        // Quién está suspendido lo decide SancionService (RN-03); acá solo se arma el detalle.
        Set<Integer> suspendidos = sancionService.obtenerJugadoresSuspendidos(torneoId, numeroFecha);
        if (suspendidos.isEmpty()) {
            return List.of();
        }

        int fechaSancion = numeroFecha - 1;
        Map<Integer, List<EventoPartido>> tarjetasPorJugador = partidoRepository
                .findByTorneoIdAndNumeroFecha(torneoId, fechaSancion).stream()
                .filter(p -> p.getEstado() == EstadoPartido.FINALIZADO)
                .flatMap(p -> eventoPartidoRepository.findByPartidoId(p.getId()).stream())
                .filter(e -> e.getTipo() != TipoEvento.GOL)
                .filter(e -> suspendidos.contains(e.getJugador().getId()))
                .collect(Collectors.groupingBy(e -> e.getJugador().getId()));

        return tarjetasPorJugador.values().stream()
                .map(tarjetas -> toResponse(tarjetas, fechaSancion))
                .sorted(Comparator.comparing(SuspendidoResponse::equipoNombre)
                        .thenComparing(SuspendidoResponse::jugadorNombre))
                .toList();
    }

    private SuspendidoResponse toResponse(List<EventoPartido> tarjetas, int fechaSancion) {
        Jugador jugador = tarjetas.get(0).getJugador();
        boolean tuvoRoja = tarjetas.stream().anyMatch(e -> e.getTipo() == TipoEvento.ROJA);
        return new SuspendidoResponse(
                jugador.getId(),
                jugador.getNombre() + " " + jugador.getApellido(),
                jugador.getEquipo().getId(),
                jugador.getEquipo().getNombre(),
                tuvoRoja ? MOTIVO_ROJA : MOTIVO_DOBLE_AMARILLA,
                fechaSancion);
    }
}
