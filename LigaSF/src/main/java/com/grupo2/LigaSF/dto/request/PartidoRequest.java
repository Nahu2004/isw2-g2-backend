package com.grupo2.LigaSF.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class PartidoRequest {

    @NotNull(message = "El ID del torneo es obligatorio")
    private Integer torneoId;

    @NotNull(message = "El número de fecha es obligatorio")
    @Min(value = 1, message = "El número de fecha debe ser al menos 1")
    private Integer numeroFecha;

    @NotNull(message = "El ID del equipo local es obligatorio")
    private Integer equipoLocalId;

    @NotNull(message = "El ID del equipo visitante es obligatorio")
    private Integer equipoVisitanteId;

    @NotNull(message = "El ID de la cancha es obligatorio")
    private Integer canchaId;

    @NotNull(message = "El ID del árbitro es obligatorio")
    private Integer arbitroId;

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHoraInicio;

    public PartidoRequest() {
    }

    public PartidoRequest(Integer torneoId, Integer numeroFecha, Integer equipoLocalId,
                          Integer equipoVisitanteId, Integer canchaId, Integer arbitroId,
                          LocalDateTime fechaHoraInicio) {
        this.torneoId = torneoId;
        this.numeroFecha = numeroFecha;
        this.equipoLocalId = equipoLocalId;
        this.equipoVisitanteId = equipoVisitanteId;
        this.canchaId = canchaId;
        this.arbitroId = arbitroId;
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public Integer getTorneoId() {
        return torneoId;
    }

    public void setTorneoId(Integer torneoId) {
        this.torneoId = torneoId;
    }

    public Integer getNumeroFecha() {
        return numeroFecha;
    }

    public void setNumeroFecha(Integer numeroFecha) {
        this.numeroFecha = numeroFecha;
    }

    public Integer getEquipoLocalId() {
        return equipoLocalId;
    }

    public void setEquipoLocalId(Integer equipoLocalId) {
        this.equipoLocalId = equipoLocalId;
    }

    public Integer getEquipoVisitanteId() {
        return equipoVisitanteId;
    }

    public void setEquipoVisitanteId(Integer equipoVisitanteId) {
        this.equipoVisitanteId = equipoVisitanteId;
    }

    public Integer getCanchaId() {
        return canchaId;
    }

    public void setCanchaId(Integer canchaId) {
        this.canchaId = canchaId;
    }

    public Integer getArbitroId() {
        return arbitroId;
    }

    public void setArbitroId(Integer arbitroId) {
        this.arbitroId = arbitroId;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }
}
