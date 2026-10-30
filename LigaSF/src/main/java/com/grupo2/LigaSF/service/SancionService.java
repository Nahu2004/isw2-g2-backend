package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.response.SancionResponse;

import java.util.List;
import java.util.Set;

public interface SancionService {

    /**
     * Verifica si un jugador se encuentra suspendido para disputar una fecha determinada en un torneo.
     *
     * <p>Según RN-03: si en un partido FINALIZADO de la fecha N, un jugador recibe
     * 1 roja o 2 amarillas en el mismo partido, queda suspendido para la fecha N+1.
     * Las amarillas de partidos distintos NO se acumulan entre sí.</p>
     */
    boolean estaJugadorSuspendido(Integer jugadorId, Integer torneoId, Integer numeroFecha);

    /**
     * Obtiene los IDs de todos los jugadores suspendidos para una fecha específica de un torneo.
     * Útil para validaciones internas rápidas.
     */
    Set<Integer> obtenerJugadoresSuspendidos(Integer torneoId, Integer numeroFecha);

    /**
     * Módulo 4 – Reglamento: Devuelve el detalle completo de cada jugador suspendido
     * para una fecha, incluyendo nombre y motivo de la sanción (RN-03).
     *
     * @param torneoId    ID del torneo.
     * @param numeroFecha Número de la fecha para la cual se consultan las sanciones.
     * @return Lista de {@link SancionResponse} con un ítem por cada jugador suspendido.
     *         Vacía si no hay suspendidos.
     */
    List<SancionResponse> listarSancionados(Integer torneoId, Integer numeroFecha);
}
