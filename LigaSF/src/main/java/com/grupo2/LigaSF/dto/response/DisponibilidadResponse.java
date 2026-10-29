package com.grupo2.LigaSF.dto.response;

/**
 * Módulo 4 – Reglamento: Respuesta de consulta de disponibilidad de horario.
 *
 * <p>Indica si un recurso (cancha o árbitro) está libre en el horario solicitado.
 * Si no está disponible, {@code motivo} describe el conflicto encontrado.</p>
 */
public class DisponibilidadResponse {

    private final boolean disponible;
    private final String motivo;

    /** Constructor para cuando el recurso está disponible. */
    public DisponibilidadResponse(boolean disponible) {
        this.disponible = disponible;
        this.motivo = null;
    }

    /** Constructor para cuando hay un conflicto de horario. */
    public DisponibilidadResponse(boolean disponible, String motivo) {
        this.disponible = disponible;
        this.motivo = motivo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public String getMotivo() {
        return motivo;
    }
}
