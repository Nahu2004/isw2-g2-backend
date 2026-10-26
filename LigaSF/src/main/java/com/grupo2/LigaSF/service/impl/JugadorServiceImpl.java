package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.JugadorRequest;
import com.grupo2.LigaSF.dto.response.JugadorResponse;
import com.grupo2.LigaSF.entity.Equipo;
import com.grupo2.LigaSF.entity.Jugador;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.EquipoRepository;
import com.grupo2.LigaSF.repository.JugadorRepository;
import com.grupo2.LigaSF.service.JugadorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JugadorServiceImpl implements JugadorService {

    private final JugadorRepository jugadorRepository;
    private final EquipoRepository equipoRepository;

    public JugadorServiceImpl(JugadorRepository jugadorRepository, EquipoRepository equipoRepository) {
        this.jugadorRepository = jugadorRepository;
        this.equipoRepository = equipoRepository;
    }

    @Override
    public JugadorResponse crear(JugadorRequest request) {
        Equipo equipo = buscarEquipo(request.getEquipoId());
        if (!equipo.getActivo()) {
            throw new BadRequestException("No se pueden agregar jugadores a un equipo desactivado");
        }

        String dniTrim = request.getDni().trim();
        if (jugadorRepository.existsByDni(dniTrim)) {
            throw new ConflictException("Ya existe un jugador registrado con el DNI " + dniTrim);
        }

        Jugador jugador = new Jugador();
        jugador.setEquipo(equipo);
        jugador.setNombre(request.getNombre().trim());
        jugador.setApellido(request.getApellido().trim());
        jugador.setDni(dniTrim);
        jugador.setNumeroCamiseta(request.getNumeroCamiseta());
        jugador.setActivo(true);

        Jugador guardado = jugadorRepository.save(jugador);
        return toResponse(guardado);
    }

    @Override
    public JugadorResponse actualizar(Integer id, JugadorRequest request) {
        Jugador jugador = buscarJugador(id);

        // RF-04: Un jugador no cambia de equipo durante el torneo
        if (request.getEquipoId() != null && !request.getEquipoId().equals(jugador.getEquipo().getId())) {
            throw new BadRequestException("Un jugador no puede cambiar de equipo durante el torneo");
        }

        String dniTrim = request.getDni().trim();
        if (jugadorRepository.existsByDniAndIdNot(dniTrim, id)) {
            throw new ConflictException("Ya existe otro jugador registrado con el DNI " + dniTrim);
        }

        jugador.setNombre(request.getNombre().trim());
        jugador.setApellido(request.getApellido().trim());
        jugador.setDni(dniTrim);
        jugador.setNumeroCamiseta(request.getNumeroCamiseta());

        Jugador actualizado = jugadorRepository.save(jugador);
        return toResponse(actualizado);
    }

    @Override
    public JugadorResponse desactivar(Integer id) {
        Jugador jugador = buscarJugador(id);
        jugador.setActivo(false); // Baja lógica (RN-09)
        Jugador actualizado = jugadorRepository.save(jugador);
        return toResponse(actualizado);
    }

    @Override
    public JugadorResponse reactivar(Integer id) {
        Jugador jugador = buscarJugador(id);
        jugador.setActivo(true);
        Jugador actualizado = jugadorRepository.save(jugador);
        return toResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public JugadorResponse obtenerPorId(Integer id) {
        Jugador jugador = buscarJugador(id);
        return toResponse(jugador);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JugadorResponse> listar(Integer equipoId, boolean soloActivos) {
        List<Jugador> jugadores;

        if (equipoId != null) {
            jugadores = soloActivos
                    ? jugadorRepository.findByEquipoIdAndActivoTrue(equipoId)
                    : jugadorRepository.findByEquipoId(equipoId);
        } else {
            jugadores = soloActivos
                    ? jugadorRepository.findByActivoTrue()
                    : jugadorRepository.findAll();
        }

        return jugadores.stream()
                .map(this::toResponse)
                .toList();
    }

    private Jugador buscarJugador(Integer id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado con ID: " + id));
    }

    private Equipo buscarEquipo(Integer equipoId) {
        return equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + equipoId));
    }

    private JugadorResponse toResponse(Jugador jugador) {
        return new JugadorResponse(
                jugador.getId(),
                jugador.getEquipo() != null ? jugador.getEquipo().getId() : null,
                jugador.getEquipo() != null ? jugador.getEquipo().getNombre() : null,
                jugador.getNombre(),
                jugador.getApellido(),
                jugador.getDni(),
                jugador.getNumeroCamiseta(),
                jugador.getActivo()
        );
    }
}
