package com.grupo2.LigaSF.dto.response;

import com.grupo2.LigaSF.entity.enums.EstadoPartido;

import java.time.LocalDateTime;

public class PartidoResponse {

    private Integer id;
    private Integer torneoId;
    private String torneoNombre;
    private Integer numeroFecha;
    private Integer equipoLocalId;
    private String equipoLocalNombre;
    private Integer equipoVisitanteId;
    private String equipoVisitanteNombre;
    private Integer canchaId;
    private String canchaNombre;
    private Integer arbitroId;
    private String arbitroNombre;
    private LocalDateTime fechaHoraInicio;
    private EstadoPartido estado;
    private Integer golesLocal;
    private Integer golesVisitante;

    public PartidoResponse() {
    }

    public PartidoResponse(Integer id, Integer torneoId, String torneoNombre, Integer numeroFecha,
                           Integer equipoLocalId, String equipoLocalNombre,
                           Integer equipoVisitanteId, String equipoVisitanteNombre,
                           Integer canchaId, String canchaNombre,
                           Integer arbitroId, String arbitroNombre,
                           LocalDateTime fechaHoraInicio, EstadoPartido estado,
                           Integer golesLocal, Integer golesVisitante) {
        this.id = id;
        this.torneoId = torneoId;
        this.torneoNombre = torneoNombre;
        this.numeroFecha = numeroFecha;
        this.equipoLocalId = equipoLocalId;
        this.equipoLocalNombre = equipoLocalNombre;
        this.equipoVisitanteId = equipoVisitanteId;
        this.equipoVisitanteNombre = equipoVisitanteNombre;
        this.canchaId = canchaId;
        this.canchaNombre = canchaNombre;
        this.arbitroId = arbitroId;
        this.arbitroNombre = arbitroNombre;
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = estado;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
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

    public String getEquipoLocalNombre() {
        return equipoLocalNombre;
    }

    public void setEquipoLocalNombre(String equipoLocalNombre) {
        this.equipoLocalNombre = equipoLocalNombre;
    }

    public Integer getEquipoVisitanteId() {
        return equipoVisitanteId;
    }

    public void setEquipoVisitanteId(Integer equipoVisitanteId) {
        this.equipoVisitanteId = equipoVisitanteId;
    }

    public String getEquipoVisitanteNombre() {
        return equipoVisitanteNombre;
    }

    public void setEquipoVisitanteNombre(String equipoVisitanteNombre) {
        this.equipoVisitanteNombre = equipoVisitanteNombre;
    }

    public Integer getCanchaId() {
        return canchaId;
    }

    public void setCanchaId(Integer canchaId) {
        this.canchaId = canchaId;
    }

    public String getCanchaNombre() {
        return canchaNombre;
    }

    public void setCanchaNombre(String canchaNombre) {
        this.canchaNombre = canchaNombre;
    }

    public Integer getArbitroId() {
        return arbitroId;
    }

    public void setArbitroId(Integer arbitroId) {
        this.arbitroId = arbitroId;
    }

    public String getArbitroNombre() {
        return arbitroNombre;
    }

    public void setArbitroNombre(String arbitroNombre) {
        this.arbitroNombre = arbitroNombre;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public EstadoPartido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPartido estado) {
        this.estado = estado;
    }

    public Integer getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(Integer golesLocal) {
        this.golesLocal = golesLocal;
    }

    public Integer getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(Integer golesVisitante) {
        this.golesVisitante = golesVisitante;
    }
}
