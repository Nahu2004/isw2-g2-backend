package com.grupo2.LigaSF.dto.response;

public class EquipoResponse {

    private Integer id;
    private Integer torneoId;
    private String torneoNombre;
    private String nombre;
    private String barrio;
    private Boolean activo;

    public EquipoResponse() {
    }

    public EquipoResponse(Integer id, Integer torneoId, String torneoNombre, String nombre, String barrio, Boolean activo) {
        this.id = id;
        this.torneoId = torneoId;
        this.torneoNombre = torneoNombre;
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

    public Integer getTorneoId() {
        return torneoId;
    }

    public void setTorneoId(Integer torneoId) {
        this.torneoId = torneoId;
    }

    public String getTorneoNombre() {
        return torneoNombre;
    }

    public void setTorneoNombre(String torneoNombre) {
        this.torneoNombre = torneoNombre;
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
