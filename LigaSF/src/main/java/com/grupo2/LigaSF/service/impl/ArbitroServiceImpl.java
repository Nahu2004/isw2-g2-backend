package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.ArbitroRequest;
import com.grupo2.LigaSF.dto.response.ArbitroResponse;
import com.grupo2.LigaSF.entity.Arbitro;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.ArbitroRepository;
import com.grupo2.LigaSF.service.ArbitroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArbitroServiceImpl implements ArbitroService {

    private final ArbitroRepository arbitroRepository;

    public ArbitroServiceImpl(ArbitroRepository arbitroRepository) {
        this.arbitroRepository = arbitroRepository;
    }

    @Override
    public ArbitroResponse crear(ArbitroRequest request) {
        String dniTrim = request.getDni().trim();
        if (arbitroRepository.existsByDni(dniTrim)) {
            throw new ConflictException("Ya existe un árbitro registrado con el DNI " + dniTrim);
        }

        Arbitro arbitro = new Arbitro(
                request.getNombre().trim(),
                request.getApellido().trim(),
                dniTrim,
                true
        );
        Arbitro guardado = arbitroRepository.save(arbitro);
        return toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ArbitroResponse obtenerPorId(Integer id) {
        Arbitro arbitro = buscarArbitro(id);
        return toResponse(arbitro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArbitroResponse> listar(boolean soloActivos) {
        List<Arbitro> arbitros = soloActivos
                ? arbitroRepository.findByActivoTrue()
                : arbitroRepository.findAll();
        return arbitros.stream().map(this::toResponse).toList();
    }

    @Override
    public ArbitroResponse desactivar(Integer id) {
        Arbitro arbitro = buscarArbitro(id);
        arbitro.setActivo(false);
        return toResponse(arbitroRepository.save(arbitro));
    }

    @Override
    public ArbitroResponse reactivar(Integer id) {
        Arbitro arbitro = buscarArbitro(id);
        arbitro.setActivo(true);
        return toResponse(arbitroRepository.save(arbitro));
    }

    private Arbitro buscarArbitro(Integer id) {
        return arbitroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Árbitro no encontrado con ID: " + id));
    }

    private ArbitroResponse toResponse(Arbitro arbitro) {
        return new ArbitroResponse(arbitro.getId(), arbitro.getNombre(), arbitro.getApellido(), arbitro.getDni(), arbitro.getActivo());
    }
}
