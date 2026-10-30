package com.grupo2.LigaSF.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TorneoRequest {

    @NotBlank(message = "El nombre del torneo es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;

    @NotNull(message = "El año del torneo es obligatorio")
    @Min(value = 2020, message = "El año no es válido")
    private Integer anio;

    public TorneoRequest() {
    }

    public TorneoRequest(String nombre, Integer anio) {
        this.nombre = nombre;
        this.anio = anio;
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
}
