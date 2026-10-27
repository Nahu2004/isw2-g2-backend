package com.grupo2.LigaSF.dto.response;

import com.grupo2.LigaSF.entity.EventoPartido;
import com.grupo2.LigaSF.entity.enums.TipoEvento;

public class EventoPartidoResponse {

    private Integer id;
    private Integer partidoId;
    private Integer jugadorId;
    private String jugadorNombreCompleto;
    private Integer equipoId;
    private String equipoNombre;
    private TipoEvento tipo;
    private Integer minuto;

    public EventoPartidoResponse() {
    }

    public static EventoPartidoResponse fromEntity(EventoPartido evento) {
        EventoPartidoResponse resp = new EventoPartidoResponse();
        resp.setId(evento.getId());
        if (evento.getPartido() != null) {
            resp.setPartidoId(evento.getPartido().getId());
        }
        if (evento.getJugador() != null) {
            resp.setJugadorId(evento.getJugador().getId());
            resp.setJugadorNombreCompleto(evento.getJugador().getNombre() + " " + evento.getJugador().getApellido());
            if (evento.getJugador().getEquipo() != null) {
                resp.setEquipoId(evento.getJugador().getEquipo().getId());
                resp.setEquipoNombre(evento.getJugador().getEquipo().getNombre());
            }
        }
        resp.setTipo(evento.getTipo());
        resp.setMinuto(evento.getMinuto());
        return resp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Integer partidoId) {
        this.partidoId = partidoId;
    }

    public Integer getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Integer jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getJugadorNombreCompleto() {
        return jugadorNombreCompleto;
    }

    public void setJugadorNombreCompleto(String jugadorNombreCompleto) {
        this.jugadorNombreCompleto = jugadorNombreCompleto;
    }

    public Integer getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Integer equipoId) {
        this.equipoId = equipoId;
    }

    public String getEquipoNombre() {
        return equipoNombre;
    }

    public void setEquipoNombre(String equipoNombre) {
        this.equipoNombre = equipoNombre;
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
