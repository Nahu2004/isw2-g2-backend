package com.grupo2.LigaSF.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CanchaRequest {

    @NotBlank(message = "El nombre de la cancha es obligatorio")
    @Size(max = 60, message = "El nombre no puede superar los 60 caracteres")
    private String nombre;

    @Size(max = 120, message = "La dirección no puede superar los 120 caracteres")
    private String direccion;

    public CanchaRequest() {
    }

    public CanchaRequest(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
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
}
