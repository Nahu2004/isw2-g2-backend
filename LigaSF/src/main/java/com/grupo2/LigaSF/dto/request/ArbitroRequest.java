package com.grupo2.LigaSF.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ArbitroRequest {

    @NotBlank(message = "El nombre del árbitro es obligatorio")
    @Size(max = 60, message = "El nombre no puede superar los 60 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido del árbitro es obligatorio")
    @Size(max = 60, message = "El apellido no puede superar los 60 caracteres")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{7,10}$", message = "El DNI debe contener entre 7 y 10 dígitos numéricos")
    private String dni;

    public ArbitroRequest() {
    }

    public ArbitroRequest(String nombre, String apellido, String dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
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
}
