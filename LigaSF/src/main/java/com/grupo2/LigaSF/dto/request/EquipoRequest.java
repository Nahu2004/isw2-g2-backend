package com.grupo2.LigaSF.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EquipoRequest {

    @NotNull(message = "El ID del torneo es obligatorio")
    private Integer torneoId;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 80, message = "El nombre no puede tener más de 80 caracteres")
    private String nombre;

    @Size(max = 80, message = "El barrio no puede tener más de 80 caracteres")
    private String barrio;

    public EquipoRequest() {
    }

    public EquipoRequest(Integer torneoId, String nombre, String barrio) {
        this.torneoId = torneoId;
        this.nombre = nombre;
        this.barrio = barrio;
    }

    public Integer getTorneoId() {
        return torneoId;
    }

    public void setTorneoId(Integer torneoId) {
        this.torneoId = torneoId;
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
}
