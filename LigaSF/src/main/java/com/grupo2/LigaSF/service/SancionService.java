package com.grupo2.LigaSF.service;

import java.util.Set;

public interface SancionService {
    /**
     * Verifica si un jugador se encuentra suspendido para disputar una fecha determinada en un torneo.
     * Según RN-03:
     * Si en un partido FINALIZADO de la fecha N, un jugador recibe 1 roja o 2 amarillas,
     * queda automáticamente suspendido para la fecha N+1.
     * Las amarillas de partidos distintos NO se acumulan.
     * La suspensión en la última fecha del torneo se pierde.
     */
    boolean estaJugadorSuspendido(Integer jugadorId, Integer torneoId, Integer numeroFecha);

    /**
     * Obtiene los IDs de todos los jugadores suspendidos para una fecha específica de un torneo.
     */
    Set<Integer> obtenerJugadoresSuspendidos(Integer torneoId, Integer numeroFecha);
}
