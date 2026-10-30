package com.grupo2.LigaSF.dto.response;

public class TorneoResponse {

    private Integer id;
    private String nombre;
    private Integer anio;
    private Boolean activo;

    public TorneoResponse() {
    }

    public TorneoResponse(Integer id, String nombre, Integer anio, Boolean activo) {
        this.id = id;
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
