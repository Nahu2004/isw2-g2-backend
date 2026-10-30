package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.entity.Jugador;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.JugadorRepository;
import com.grupo2.LigaSF.service.BloqueoJugadorService;
import com.grupo2.LigaSF.service.SancionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Módulo 4 – Reglamento: Implementación del bloqueo de jugadores.
 *
 * <p>Combina dos validaciones independientes para determinar
 * si un jugador puede participar en una fecha:</p>
 * <ol>
 *   <li>Que el jugador esté activo en el sistema (baja lógica).</li>
 *   <li>Que no tenga suspensión por tarjetas de la fecha anterior (RN-03).</li>
 * </ol>
 *
 * <p>Este servicio es el <strong>punto de integración</strong> que deben
 * consumir el Módulo 2 (eventos de partido) y el Módulo 3 (inscripción)
 * antes de habilitar a un jugador.</p>
 */
@Service
@Transactional(readOnly = true)
public class BloqueoJugadorServiceImpl implements BloqueoJugadorService {

    private final SancionService sancionService;
    private final JugadorRepository jugadorRepository;

    public BloqueoJugadorServiceImpl(SancionService sancionService,
                                     JugadorRepository jugadorRepository) {
        this.sancionService = sancionService;
        this.jugadorRepository = jugadorRepository;
    }

    // -------------------------------------------------------------------------
    // estaJugadorBloqueado — consulta booleana (para endpoints de consulta)
    // -------------------------------------------------------------------------

    @Override
    public boolean estaJugadorBloqueado(Integer jugadorId, Integer torneoId, Integer numeroFecha) {
        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Jugador no encontrado con ID: " + jugadorId));

        // Bloqueado si está desactivado (baja lógica)
        if (!jugador.getActivo()) {
            return true;
        }

        // Bloqueado si tiene suspensión por tarjetas (RN-03)
        return sancionService.estaJugadorSuspendido(jugadorId, torneoId, numeroFecha);
    }

    // -------------------------------------------------------------------------
    // verificarHabilitacion — lanza excepción (para validaciones en otros módulos)
    // -------------------------------------------------------------------------

    @Override
    public void verificarHabilitacion(Integer jugadorId, Integer torneoId, Integer numeroFecha) {
        Jugador jugador = jugadorRepository.findById(jugadorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Jugador no encontrado con ID: " + jugadorId));

        // Validación 1: el jugador debe estar activo
        if (!jugador.getActivo()) {
            throw new BadRequestException(
                    "El jugador '" + jugador.getNombre() + " " + jugador.getApellido()
                    + "' está desactivado y no puede participar en el partido."
            );
        }

        // Validación 2: el jugador no debe tener suspensión por tarjetas (RN-03)
        if (sancionService.estaJugadorSuspendido(jugadorId, torneoId, numeroFecha)) {
            throw new ConflictException(
                    "El jugador '" + jugador.getNombre() + " " + jugador.getApellido()
                    + "' está suspendido para la fecha " + numeroFecha
                    + " del torneo (RN-03) y no puede ser inscripto ni participar."
            );
        }
    }
}
