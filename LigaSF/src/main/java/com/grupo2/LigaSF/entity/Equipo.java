package com.grupo2.LigaSF.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "equipo",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_equipo_nombre_torneo", columnNames = {"torneo_id", "nombre"})
    }
)
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "barrio", length = 80)
    private String barrio;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Equipo() {
    }

    public Equipo(Torneo torneo, String nombre, String barrio, Boolean activo) {
        this.torneo = torneo;
        this.nombre = nombre;
        this.barrio = barrio;
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
