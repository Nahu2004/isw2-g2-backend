package com.grupo2.LigaSF.service;

/**
 * Módulo 4 – Reglamento: Bloqueo de jugadores suspendidos.
 *
 * <p>Punto de integración entre el Módulo 4 (Reglamento) y los módulos
 * que inscriben o cargan jugadores en partidos (Módulos 2 y 3).</p>
 *
 * <p>Regla de negocio aplicada (RN-03 + RN-04):</p>
 * <ul>
 *   <li>Un jugador suspendido para la fecha N no puede ser inscripto
 *       ni cargado como participante en ningún partido de esa fecha.</li>
 *   <li>Un jugador desactivado (baja lógica) tampoco puede participar.</li>
 * </ul>
 */
public interface BloqueoJugadorService {

    /**
     * Verifica si un jugador está bloqueado para participar en una fecha de un torneo.
     *
     * <p>Un jugador está bloqueado si:</p>
     * <ul>
     *   <li>Está suspendido por tarjetas de la fecha anterior (RN-03), o</li>
     *   <li>Está desactivado en el sistema (baja lógica).</li>
     * </ul>
     *
     * @param jugadorId   ID del jugador a verificar.
     * @param torneoId    ID del torneo en curso.
     * @param numeroFecha Número de la fecha a jugar.
     * @return {@code true} si el jugador está bloqueado y NO puede participar.
     *         {@code false} si está habilitado.
     */
    boolean estaJugadorBloqueado(Integer jugadorId, Integer torneoId, Integer numeroFecha);

    /**
     * Verifica que un jugador esté habilitado y lanza una excepción si no lo está.
     *
     * <p>Este método es el que deben llamar los otros módulos (Módulo 2 y 3)
     * antes de inscribir o cargar un jugador en un partido.</p>
     *
     * @param jugadorId   ID del jugador a verificar.
     * @param torneoId    ID del torneo en curso.
     * @param numeroFecha Número de la fecha a jugar.
     * @throws com.grupo2.LigaSF.exception.ConflictException si el jugador
     *         está suspendido.
     * @throws com.grupo2.LigaSF.exception.BadRequestException si el jugador
     *         está desactivado.
     */
    void verificarHabilitacion(Integer jugadorId, Integer torneoId, Integer numeroFecha);
}
