package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.EventoPartidoRequest;
import com.grupo2.LigaSF.dto.response.EventoPartidoResponse;
import com.grupo2.LigaSF.entity.EventoPartido;
import com.grupo2.LigaSF.entity.Jugador;
import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.EventoPartidoRepository;
import com.grupo2.LigaSF.repository.JugadorRepository;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.EventoPartidoService;
import com.grupo2.LigaSF.service.SancionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventoPartidoServiceImpl implements EventoPartidoService {

    private final EventoPartidoRepository eventoPartidoRepository;
    private final PartidoRepository partidoRepository;
    private final JugadorRepository jugadorRepository;
    private final SancionService sancionService;

    public EventoPartidoServiceImpl(EventoPartidoRepository eventoPartidoRepository,
                                   PartidoRepository partidoRepository,
                                   JugadorRepository jugadorRepository,
                                   SancionService sancionService) {
        this.eventoPartidoRepository = eventoPartidoRepository;
        this.partidoRepository = partidoRepository;
        this.jugadorRepository = jugadorRepository;
        this.sancionService = sancionService;
    }

    @Override
    @Transactional
    public EventoPartidoResponse registrarEvento(Integer partidoId, EventoPartidoRequest request) {
        // Validar existencia del partido
        Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + partidoId));

        // RN-04: Solo se pueden registrar eventos si el partido se encuentra estrictamente en estado EN_JUEGO
        if (partido.getEstado() != EstadoPartido.EN_JUEGO) {
            throw new BadRequestException("Solo se pueden registrar eventos cuando el partido está en estado EN_JUEGO. Estado actual: " + partido.getEstado());
        }

        // Validar existencia del jugador
        Jugador jugador = jugadorRepository.findById(request.getJugadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado con id: " + request.getJugadorId()));

        // RN-04: El jugador debe estar activo
        if (Boolean.FALSE.equals(jugador.getActivo())) {
            throw new BadRequestException("No se pueden registrar eventos para un jugador inactivo");
        }

        // RN-04: El jugador debe pertenecer obligatoriamente al equipo local o visitante de ese partido
        Integer equipoJugadorId = jugador.getEquipo() != null ? jugador.getEquipo().getId() : null;
        Integer equipoLocalId = partido.getEquipoLocal() != null ? partido.getEquipoLocal().getId() : null;
        Integer equipoVisitanteId = partido.getEquipoVisitante() != null ? partido.getEquipoVisitante().getId() : null;

        if (equipoJugadorId == null ||
                (!Objects.equals(equipoJugadorId, equipoLocalId) && !Objects.equals(equipoJugadorId, equipoVisitanteId))) {
            throw new BadRequestException("El jugador no pertenece a ninguno de los dos equipos participantes del partido");
        }

        // RN-04: Validar que el jugador no esté suspendido para esa fecha
        Integer torneoId = partido.getTorneo() != null ? partido.getTorneo().getId() : null;
        if (sancionService.estaJugadorSuspendido(jugador.getId(), torneoId, partido.getNumeroFecha())) {
            throw new BadRequestException("El jugador se encuentra suspendido para la fecha " + partido.getNumeroFecha());
        }

        EventoPartido evento = new EventoPartido(partido, jugador, request.getTipo(), request.getMinuto());
        EventoPartido guardado = eventoPartidoRepository.save(evento);

        return EventoPartidoResponse.fromEntity(guardado);
    }

    @Override
    @Transactional
    public void eliminarEvento(Integer eventoId) {
        EventoPartido evento = eventoPartidoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + eventoId));

        Partido partido = evento.getPartido();
        // RN-04: Corregir/eliminar exclusivamente mientras el partido siga EN_JUEGO
        if (partido == null || partido.getEstado() != EstadoPartido.EN_JUEGO) {
            throw new BadRequestException("Solo se pueden corregir o eliminar eventos mientras el partido está EN_JUEGO");
        }

        eventoPartidoRepository.delete(evento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoPartidoResponse> listarPorPartido(Integer partidoId) {
        if (!partidoRepository.existsById(partidoId)) {
            throw new ResourceNotFoundException("Partido no encontrado con id: " + partidoId);
        }
        return eventoPartidoRepository.findByPartidoId(partidoId).stream()
                .map(EventoPartidoResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
