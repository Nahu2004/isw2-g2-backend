package com.grupo2.LigaSF.dto.response;

public record SuspendidoResponse(
        Integer jugadorId,
        String jugadorNombre,
        Integer equipoId,
        String equipoNombre,
        String motivo,
        Integer fechaSancion) {
}
