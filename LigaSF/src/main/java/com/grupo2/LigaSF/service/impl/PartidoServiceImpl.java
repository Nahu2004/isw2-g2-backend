package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.PartidoRequest;
import com.grupo2.LigaSF.dto.response.PartidoResponse;
import com.grupo2.LigaSF.entity.*;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.entity.enums.TipoEvento;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.*;
import com.grupo2.LigaSF.service.PartidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PartidoServiceImpl implements PartidoService {

    private final PartidoRepository partidoRepository;
    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final CanchaRepository canchaRepository;
    private final ArbitroRepository arbitroRepository;
    private final EventoPartidoRepository eventoPartidoRepository;
    private final com.grupo2.LigaSF.service.HorarioValidacionService horarioValidacionService;

    public PartidoServiceImpl(PartidoRepository partidoRepository,
                              TorneoRepository torneoRepository,
                              EquipoRepository equipoRepository,
                              CanchaRepository canchaRepository,
                              ArbitroRepository arbitroRepository,
                              EventoPartidoRepository eventoPartidoRepository,
                              com.grupo2.LigaSF.service.HorarioValidacionService horarioValidacionService) {
        this.partidoRepository = partidoRepository;
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
        this.canchaRepository = canchaRepository;
        this.arbitroRepository = arbitroRepository;
        this.eventoPartidoRepository = eventoPartidoRepository;
        this.horarioValidacionService = horarioValidacionService;
    }

    @Override
    public PartidoResponse programar(PartidoRequest request) {
        validarEquiposDistintos(request);

        Torneo torneo = buscarTorneo(request.getTorneoId());
        Equipo local = buscarEquipo(request.getEquipoLocalId(), "local");
        Equipo visitante = buscarEquipo(request.getEquipoVisitanteId(), "visitante");
        Cancha cancha = buscarCancha(request.getCanchaId());
        Arbitro arbitro = buscarArbitro(request.getArbitroId());

        validarPertenenciaTorneo(torneo, local, visitante);
        validarActivos(local, visitante, cancha, arbitro);
        validarEquipoNoJuegaDosVecesEnFecha(torneo.getId(), request.getNumeroFecha(), local, visitante, null);
        validarSolapamientoCancha(cancha, request.getFechaHoraInicio(), null);
        validarSolapamientoArbitro(arbitro, request.getFechaHoraInicio(), null);

        Partido partido = new Partido(
                torneo,
                request.getNumeroFecha(),
                local,
                visitante,
                cancha,
                arbitro,
                request.getFechaHoraInicio(),
                EstadoPartido.PROGRAMADO
        );

        Partido guardado = partidoRepository.save(partido);
        return toResponse(guardado);
    }

    @Override
    public PartidoResponse actualizar(Integer id, PartidoRequest request) {
        Partido partido = buscarPartido(id);

        // RF-06: Solo en estado PROGRAMADO se modifica o borra
        if (partido.getEstado() != EstadoPartido.PROGRAMADO) {
            throw new BadRequestException("Solo se pueden modificar partidos en estado PROGRAMADO");
        }

        validarEquiposDistintos(request);

        Torneo torneo = buscarTorneo(request.getTorneoId());
        Equipo local = buscarEquipo(request.getEquipoLocalId(), "local");
        Equipo visitante = buscarEquipo(request.getEquipoVisitanteId(), "visitante");
        Cancha cancha = buscarCancha(request.getCanchaId());
        Arbitro arbitro = buscarArbitro(request.getArbitroId());

        validarPertenenciaTorneo(torneo, local, visitante);
        validarActivos(local, visitante, cancha, arbitro);
        validarEquipoNoJuegaDosVecesEnFecha(torneo.getId(), request.getNumeroFecha(), local, visitante, id);
        validarSolapamientoCancha(cancha, request.getFechaHoraInicio(), id);
        validarSolapamientoArbitro(arbitro, request.getFechaHoraInicio(), id);

        partido.setTorneo(torneo);
        partido.setNumeroFecha(request.getNumeroFecha());
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
        partido.setCancha(cancha);
        partido.setArbitro(arbitro);
        partido.setFechaHoraInicio(request.getFechaHoraInicio());

        Partido actualizado = partidoRepository.save(partido);
        return toResponse(actualizado);
    }

    @Override
    public void eliminar(Integer id) {
        Partido partido = buscarPartido(id);

        // RF-06: Solo en estado PROGRAMADO se modifica o borra
        if (partido.getEstado() != EstadoPartido.PROGRAMADO) {
            throw new BadRequestException("Solo se pueden borrar partidos en estado PROGRAMADO");
        }

        partidoRepository.delete(partido);
    }

    @Override
    @Transactional(readOnly = true)
    public PartidoResponse obtenerPorId(Integer id) {
        Partido partido = buscarPartido(id);
        return toResponse(partido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> listar(Integer torneoId, Integer numeroFecha) {
        List<Partido> partidos;

        if (torneoId != null && numeroFecha != null) {
            partidos = partidoRepository.findByTorneoIdAndNumeroFechaOrderByFechaHoraInicioAsc(torneoId, numeroFecha);
        } else if (torneoId != null) {
            partidos = partidoRepository.findByTorneoIdOrderByNumeroFechaAscFechaHoraInicioAsc(torneoId);
        } else {
            partidos = partidoRepository.findAll();
        }

        return partidos.stream().map(this::toResponse).toList();
    }

    // ================= Validaciones de Negocio =================

    private void validarEquiposDistintos(PartidoRequest request) {
        if (request.getEquipoLocalId().equals(request.getEquipoVisitanteId())) {
            throw new BadRequestException("El equipo local y el equipo visitante deben ser distintos");
        }
    }

    private void validarPertenenciaTorneo(Torneo torneo, Equipo local, Equipo visitante) {
        if (!local.getTorneo().getId().equals(torneo.getId()) || !visitante.getTorneo().getId().equals(torneo.getId())) {
            throw new BadRequestException("Ambos equipos deben pertenecer al torneo seleccionado");
        }
    }

    private void validarActivos(Equipo local, Equipo visitante, Cancha cancha, Arbitro arbitro) {
        if (!local.getActivo()) {
            throw new BadRequestException("El equipo local '" + local.getNombre() + "' está desactivado");
        }
        if (!visitante.getActivo()) {
            throw new BadRequestException("El equipo visitante '" + visitante.getNombre() + "' está desactivado");
        }
        if (!cancha.getActivo()) {
            throw new BadRequestException("La cancha '" + cancha.getNombre() + "' está desactivada");
        }
        if (!arbitro.getActivo()) {
            throw new BadRequestException("El árbitro '" + arbitro.getNombre() + " " + arbitro.getApellido() + "' está desactivado");
        }
    }

    private void validarEquipoNoJuegaDosVecesEnFecha(Integer torneoId, Integer numeroFecha,
                                                     Equipo local, Equipo visitante, Integer idExcluido) {
        List<Partido> partidosLocal = partidoRepository.findPartidosEquipoEnFecha(torneoId, numeroFecha, local.getId());
        for (Partido p : partidosLocal) {
            if (p.getEstado() != EstadoPartido.SUSPENDIDO && (idExcluido == null || !p.getId().equals(idExcluido))) {
                throw new ConflictException("El equipo local '" + local.getNombre() + "' ya tiene un partido en la fecha " + numeroFecha);
            }
        }

        List<Partido> partidosVisitante = partidoRepository.findPartidosEquipoEnFecha(torneoId, numeroFecha, visitante.getId());
        for (Partido p : partidosVisitante) {
            if (p.getEstado() != EstadoPartido.SUSPENDIDO && (idExcluido == null || !p.getId().equals(idExcluido))) {
                throw new ConflictException("El equipo visitante '" + visitante.getNombre() + "' ya tiene un partido en la fecha " + numeroFecha);
            }
        }
    }

    // RN-01 y RN-02: delegado al Módulo 4 - Reglamento (HorarioValidacionService)
    private void validarSolapamientoCancha(Cancha cancha, LocalDateTime inicio, Integer idExcluido) {
        horarioValidacionService.validarDisponibilidadCancha(cancha.getId(), inicio, idExcluido);
    }

    private void validarSolapamientoArbitro(Arbitro arbitro, LocalDateTime inicio, Integer idExcluido) {
        horarioValidacionService.validarDisponibilidadArbitro(arbitro.getId(), inicio, idExcluido);
    }

    private Partido buscarPartido(Integer id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con ID: " + id));
    }

    private Torneo buscarTorneo(Integer torneoId) {
        return torneoRepository.findById(torneoId)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con ID: " + torneoId));
    }

    private Equipo buscarEquipo(Integer equipoId, String rol) {
        return equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo " + rol + " no encontrado con ID: " + equipoId));
    }

    private Cancha buscarCancha(Integer canchaId) {
        return canchaRepository.findById(canchaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha no encontrada con ID: " + canchaId));
    }

    private Arbitro buscarArbitro(Integer arbitroId) {
        return arbitroRepository.findById(arbitroId)
                .orElseThrow(() -> new ResourceNotFoundException("Árbitro no encontrado con ID: " + arbitroId));
    }

    private PartidoResponse toResponse(Partido partido) {
        int golesLocal = 0;
        int golesVisitante = 0;

        List<EventoPartido> eventos = eventoPartidoRepository.findByPartidoId(partido.getId());
        for (EventoPartido e : eventos) {
            if (e.getTipo() == TipoEvento.GOL && e.getJugador() != null && e.getJugador().getEquipo() != null) {
                if (e.getJugador().getEquipo().getId().equals(partido.getEquipoLocal().getId())) {
                    golesLocal++;
                } else if (e.getJugador().getEquipo().getId().equals(partido.getEquipoVisitante().getId())) {
                    golesVisitante++;
                }
            }
        }

        return new PartidoResponse(
                partido.getId(),
                partido.getTorneo().getId(),
                partido.getTorneo().getNombre(),
                partido.getNumeroFecha(),
                partido.getEquipoLocal().getId(),
                partido.getEquipoLocal().getNombre(),
                partido.getEquipoVisitante().getId(),
                partido.getEquipoVisitante().getNombre(),
                partido.getCancha().getId(),
                partido.getCancha().getNombre(),
                partido.getArbitro().getId(),
                partido.getArbitro().getNombre() + " " + partido.getArbitro().getApellido(),
                partido.getFechaHoraInicio(),
                partido.getEstado(),
                golesLocal,
                golesVisitante
        );
    }

    @Override
    public PartidoResponse cambiarEstado(Integer id, com.grupo2.LigaSF.dto.request.CambiarEstadoPartidoRequest request) {
        Partido partido = buscarPartido(id);

        EstadoPartido actual = partido.getEstado();
        EstadoPartido nuevo = request.getEstado();

        if (actual == nuevo) {
            return toResponse(partido);
        }

        // RN-05: Validar que FINALIZADO y SUSPENDIDO sean irreversibles
        if (actual == EstadoPartido.FINALIZADO || actual == EstadoPartido.SUSPENDIDO) {
            throw new BadRequestException("No se puede cambiar el estado de un partido que ya se encuentra " + actual);
        }

        // Transiciones permitidas:
        // PROGRAMADO -> EN_JUEGO
        // PROGRAMADO -> SUSPENDIDO
        // EN_JUEGO -> FINALIZADO
        // EN_JUEGO -> SUSPENDIDO
        boolean transicionValida = false;

        if (actual == EstadoPartido.PROGRAMADO) {
            if (nuevo == EstadoPartido.EN_JUEGO || nuevo == EstadoPartido.SUSPENDIDO) {
                transicionValida = true;
            }
        } else if (actual == EstadoPartido.EN_JUEGO) {
            if (nuevo == EstadoPartido.FINALIZADO || nuevo == EstadoPartido.SUSPENDIDO) {
                transicionValida = true;
            }
        }

        if (!transicionValida) {
            throw new BadRequestException("Transición de estado no válida de " + actual + " a " + nuevo);
        }

        partido.setEstado(nuevo);
        Partido actualizado = partidoRepository.save(partido);

        return toResponse(actualizado);
    }
}