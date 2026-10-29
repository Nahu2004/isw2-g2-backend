package com.grupo2.LigaSF.dto.response;

/**
 * Módulo 4 – Reglamento: Respuesta de sanción individual.
 *
 * <p>Representa la suspensión de un jugador para una fecha específica de un torneo,
 * incluyendo el motivo que generó la sanción según RN-03.</p>
 */
public class SancionResponse {

    /** ID del jugador suspendido. */
    private final Integer jugadorId;

    /** Nombre completo del jugador (nombre + apellido). */
    private final String nombreJugador;

    /** Número de la fecha para la cual el jugador está suspendido. */
    private final Integer fechaSuspendido;

    /**
     * Motivo de la suspensión. Valores posibles:
     * <ul>
     *   <li>{@code "ROJA"} — recibió tarjeta roja en la fecha anterior.</li>
     *   <li>{@code "DOS_AMARILLAS"} — recibió 2 amarillas en el mismo partido de la fecha anterior.</li>
     * </ul>
     */
    private final String motivo;

    public SancionResponse(Integer jugadorId, String nombreJugador, Integer fechaSuspendido, String motivo) {
        this.jugadorId = jugadorId;
        this.nombreJugador = nombreJugador;
        this.fechaSuspendido = fechaSuspendido;
        this.motivo = motivo;
    }

    public Integer getJugadorId() {
        return jugadorId;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public Integer getFechaSuspendido() {
        return fechaSuspendido;
    }

    public String getMotivo() {
        return motivo;
    }
}
