package com.grupo2.LigaSF.dto.response;

public class JugadorResponse {

    private Integer id;
    private Integer equipoId;
    private String equipoNombre;
    private String nombre;
    private String apellido;
    private String dni;
    private Integer numeroCamiseta;
    private Boolean activo;

    public JugadorResponse() {
    }

    public JugadorResponse(Integer id, Integer equipoId, String equipoNombre, String nombre,
                           String apellido, String dni, Integer numeroCamiseta, Boolean activo) {
        this.id = id;
        this.equipoId = equipoId;
        this.equipoNombre = equipoNombre;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.numeroCamiseta = numeroCamiseta;
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Integer getNumeroCamiseta() {
        return numeroCamiseta;
    }

    public void setNumeroCamiseta(Integer numeroCamiseta) {
        this.numeroCamiseta = numeroCamiseta;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
