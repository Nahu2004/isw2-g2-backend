package com.grupo2.LigaSF.dto.request;

import com.grupo2.LigaSF.entity.enums.TipoEvento;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EventoPartidoRequest {

    @NotNull(message = "El ID del jugador es obligatorio")
    private Integer jugadorId;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipo;

    @NotNull(message = "El minuto es obligatorio")
    @Min(value = 1, message = "El minuto debe ser entre 1 y 120")
    @Max(value = 120, message = "El minuto debe ser entre 1 y 120")
    private Integer minuto;

    public EventoPartidoRequest() {
    }

    public EventoPartidoRequest(Integer jugadorId, TipoEvento tipo, Integer minuto) {
        this.jugadorId = jugadorId;
        this.tipo = tipo;
        this.minuto = minuto;
    }

    public Integer getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Integer jugadorId) {
        this.jugadorId = jugadorId;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public Integer getMinuto() {
        return minuto;
    }

    public void setMinuto(Integer minuto) {
        this.minuto = minuto;
    }
}
