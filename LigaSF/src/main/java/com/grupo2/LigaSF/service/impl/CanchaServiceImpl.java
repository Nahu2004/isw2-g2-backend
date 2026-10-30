package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.CanchaRequest;
import com.grupo2.LigaSF.dto.response.CanchaResponse;
import com.grupo2.LigaSF.entity.Cancha;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.CanchaRepository;
import com.grupo2.LigaSF.service.CanchaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CanchaServiceImpl implements CanchaService {

    private final CanchaRepository canchaRepository;

    public CanchaServiceImpl(CanchaRepository canchaRepository) {
        this.canchaRepository = canchaRepository;
    }

    @Override
    public CanchaResponse crear(CanchaRequest request) {
        String nombreTrim = request.getNombre().trim();
        if (canchaRepository.existsByNombreIgnoreCase(nombreTrim)) {
            throw new ConflictException("Ya existe una cancha con el nombre '" + nombreTrim + "'");
        }

        Cancha cancha = new Cancha(
                nombreTrim,
                request.getDireccion() != null ? request.getDireccion().trim() : null,
                true
        );
        Cancha guardada = canchaRepository.save(cancha);
        return toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CanchaResponse obtenerPorId(Integer id) {
        Cancha cancha = buscarCancha(id);
        return toResponse(cancha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CanchaResponse> listar(boolean soloActivos) {
        List<Cancha> canchas = soloActivos
                ? canchaRepository.findByActivoTrue()
                : canchaRepository.findAll();
        return canchas.stream().map(this::toResponse).toList();
    }

    @Override
    public CanchaResponse desactivar(Integer id) {
        Cancha cancha = buscarCancha(id);
        cancha.setActivo(false);
        return toResponse(canchaRepository.save(cancha));
    }

    @Override
    public CanchaResponse reactivar(Integer id) {
        Cancha cancha = buscarCancha(id);
        cancha.setActivo(true);
        return toResponse(canchaRepository.save(cancha));
    }

    private Cancha buscarCancha(Integer id) {
        return canchaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha no encontrada con ID: " + id));
    }

    private CanchaResponse toResponse(Cancha cancha) {
        return new CanchaResponse(cancha.getId(), cancha.getNombre(), cancha.getDireccion(), cancha.getActivo());
    }
}
