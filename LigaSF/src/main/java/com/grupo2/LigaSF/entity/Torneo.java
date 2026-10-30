package com.grupo2.LigaSF.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "torneo")
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Torneo() {
    }

    public Torneo(String nombre, Integer anio, Boolean activo) {
        this.nombre = nombre;
        this.anio = anio;
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
