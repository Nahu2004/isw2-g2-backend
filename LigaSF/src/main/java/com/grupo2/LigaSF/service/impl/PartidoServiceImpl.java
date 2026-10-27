package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.request.CambiarEstadoPartidoRequest;
import com.grupo2.LigaSF.dto.response.PartidoResponse;
import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.PartidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidoServiceImpl implements PartidoService {

    private final PartidoRepository partidoRepository;

    public PartidoServiceImpl(PartidoRepository partidoRepository) {
        this.partidoRepository = partidoRepository;
    }

    @Override
    @Transactional
    public PartidoResponse cambiarEstado(Integer id, CambiarEstadoPartidoRequest request) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));

        EstadoPartido actual = partido.getEstado();
        EstadoPartido nuevo = request.getEstado();

        if (actual == nuevo) {
            return PartidoResponse.fromEntity(partido);
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

        return PartidoResponse.fromEntity(actualizado);
    }
}
