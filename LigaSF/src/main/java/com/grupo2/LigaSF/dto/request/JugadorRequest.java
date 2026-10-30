package com.grupo2.LigaSF.dto.request;

import jakarta.validation.constraints.*;

public class JugadorRequest {

    @NotNull(message = "El ID del equipo es obligatorio")
    private Integer equipoId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre no puede tener más de 60 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60, message = "El apellido no puede tener más de 60 caracteres")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{7,10}$", message = "El DNI debe contener entre 7 y 10 dígitos numéricos")
    private String dni;

    @NotNull(message = "El número de camiseta es obligatorio")
    @Min(value = 1, message = "El número de camiseta debe ser mayor o igual a 1")
    @Max(value = 99, message = "El número de camiseta debe ser menor o igual a 99")
    private Integer numeroCamiseta;

    public JugadorRequest() {
    }

    public JugadorRequest(Integer equipoId, String nombre, String apellido, String dni, Integer numeroCamiseta) {
        this.equipoId = equipoId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.numeroCamiseta = numeroCamiseta;
    }

    public Integer getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Integer equipoId) {
        this.equipoId = equipoId;
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
}
