package com.grupo2.LigaSF.entity;

import com.grupo2.LigaSF.entity.enums.TipoEvento;
import jakarta.persistence.*;

@Entity
@Table(name = "evento_partido")
public class EventoPartido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoEvento tipo;

    @Column(name = "minuto", nullable = false)
    private Integer minuto;

    public EventoPartido() {
    }

    public EventoPartido(Partido partido, Jugador jugador, TipoEvento tipo, Integer minuto) {
        this.partido = partido;
        this.jugador = jugador;
        this.tipo = tipo;
        this.minuto = minuto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
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
