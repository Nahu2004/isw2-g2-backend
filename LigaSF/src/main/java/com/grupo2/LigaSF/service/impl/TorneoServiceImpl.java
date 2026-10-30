package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.TorneoRequest;
import com.grupo2.LigaSF.dto.response.TorneoResponse;
import com.grupo2.LigaSF.entity.Torneo;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.TorneoRepository;
import com.grupo2.LigaSF.service.TorneoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TorneoServiceImpl implements TorneoService {

    private final TorneoRepository torneoRepository;

    public TorneoServiceImpl(TorneoRepository torneoRepository) {
        this.torneoRepository = torneoRepository;
    }

    @Override
    public TorneoResponse crear(TorneoRequest request) {
        Torneo torneo = new Torneo(request.getNombre().trim(), request.getAnio(), true);
        Torneo guardado = torneoRepository.save(torneo);
        return toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public TorneoResponse obtenerPorId(Integer id) {
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con ID: " + id));
        return toResponse(torneo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TorneoResponse> listar(boolean soloActivos) {
        List<Torneo> torneos = soloActivos
                ? torneoRepository.findByActivoTrue()
                : torneoRepository.findAll();
        return torneos.stream().map(this::toResponse).toList();
    }

    private TorneoResponse toResponse(Torneo torneo) {
        return new TorneoResponse(torneo.getId(), torneo.getNombre(), torneo.getAnio(), torneo.getActivo());
    }
}
