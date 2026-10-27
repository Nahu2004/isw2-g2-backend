package com.grupo2.LigaSF.dto.request;

import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import jakarta.validation.constraints.NotNull;

public class CambiarEstadoPartidoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoPartido estado;

    public CambiarEstadoPartidoRequest() {
    }

    public CambiarEstadoPartidoRequest(EstadoPartido estado) {
        this.estado = estado;
    }

    public EstadoPartido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPartido estado) {
        this.estado = estado;
    }
}
