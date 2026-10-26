package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.EquipoRequest;
import com.grupo2.LigaSF.dto.response.EquipoResponse;
import com.grupo2.LigaSF.entity.Equipo;
import com.grupo2.LigaSF.entity.Torneo;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.EquipoRepository;
import com.grupo2.LigaSF.repository.TorneoRepository;
import com.grupo2.LigaSF.service.EquipoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final TorneoRepository torneoRepository;

    public EquipoServiceImpl(EquipoRepository equipoRepository, TorneoRepository torneoRepository) {
        this.equipoRepository = equipoRepository;
        this.torneoRepository = torneoRepository;
    }

    @Override
    public EquipoResponse crear(EquipoRequest request) {
        Torneo torneo = buscarTorneo(request.getTorneoId());

        String nombreTrim = request.getNombre().trim();
        if (equipoRepository.existsByTorneoIdAndNombreIgnoreCase(torneo.getId(), nombreTrim)) {
            throw new ConflictException("Ya existe un equipo con el nombre '" + nombreTrim + "' en este torneo");
        }

        Equipo equipo = new Equipo();
        equipo.setTorneo(torneo);
        equipo.setNombre(nombreTrim);
        equipo.setBarrio(request.getBarrio() != null ? request.getBarrio().trim() : null);
        equipo.setActivo(true);

        Equipo guardado = equipoRepository.save(equipo);
        return toResponse(guardado);
    }

    @Override
    public EquipoResponse actualizar(Integer id, EquipoRequest request) {
        Equipo equipo = buscarEquipo(id);
        Torneo torneo = buscarTorneo(request.getTorneoId());

        String nombreTrim = request.getNombre().trim();
        if (equipoRepository.existsByTorneoIdAndNombreIgnoreCaseAndIdNot(torneo.getId(), nombreTrim, id)) {
            throw new ConflictException("Ya existe otro equipo con el nombre '" + nombreTrim + "' en este torneo");
        }

        equipo.setTorneo(torneo);
        equipo.setNombre(nombreTrim);
        equipo.setBarrio(request.getBarrio() != null ? request.getBarrio().trim() : null);

        Equipo actualizado = equipoRepository.save(equipo);
        return toResponse(actualizado);
    }

    @Override
    public EquipoResponse desactivar(Integer id) {
        Equipo equipo = buscarEquipo(id);
        equipo.setActivo(false); // Baja lógica (RN-09)
        Equipo actualizado = equipoRepository.save(equipo);
        return toResponse(actualizado);
    }

    @Override
    public EquipoResponse reactivar(Integer id) {
        Equipo equipo = buscarEquipo(id);
        equipo.setActivo(true);
        Equipo actualizado = equipoRepository.save(equipo);
        return toResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipoResponse obtenerPorId(Integer id) {
        Equipo equipo = buscarEquipo(id);
        return toResponse(equipo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipoResponse> listar(Integer torneoId, boolean soloActivos) {
        List<Equipo> equipos;

        if (torneoId != null) {
            equipos = soloActivos
                    ? equipoRepository.findByTorneoIdAndActivoTrue(torneoId)
                    : equipoRepository.findByTorneoId(torneoId);
        } else {
            equipos = soloActivos
                    ? equipoRepository.findByActivoTrue()
                    : equipoRepository.findAll();
        }

        return equipos.stream()
                .map(this::toResponse)
                .toList();
    }

    private Equipo buscarEquipo(Integer id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + id));
    }

    private Torneo buscarTorneo(Integer torneoId) {
        return torneoRepository.findById(torneoId)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con ID: " + torneoId));
    }

    private EquipoResponse toResponse(Equipo equipo) {
        return new EquipoResponse(
                equipo.getId(),
                equipo.getTorneo() != null ? equipo.getTorneo().getId() : null,
                equipo.getTorneo() != null ? equipo.getTorneo().getNombre() : null,
                equipo.getNombre(),
                equipo.getBarrio(),
                equipo.getActivo()
        );
    }
}
