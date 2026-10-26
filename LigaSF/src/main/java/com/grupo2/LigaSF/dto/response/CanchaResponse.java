package com.grupo2.LigaSF.dto.response;

public class CanchaResponse {

    private Integer id;
    private String nombre;
    private String direccion;
    private Boolean activo;

    public CanchaResponse() {
    }

    public CanchaResponse(Integer id, String nombre, String direccion, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
