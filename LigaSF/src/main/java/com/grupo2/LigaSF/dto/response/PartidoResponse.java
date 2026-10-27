package com.grupo2.LigaSF.dto.response;

import com.grupo2.LigaSF.entity.Partido;
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

    public PartidoResponse() {
    }

    public static PartidoResponse fromEntity(Partido partido) {
        PartidoResponse resp = new PartidoResponse();
        resp.setId(partido.getId());
        if (partido.getTorneo() != null) {
            resp.setTorneoId(partido.getTorneo().getId());
            resp.setTorneoNombre(partido.getTorneo().getNombre());
        }
        resp.setNumeroFecha(partido.getNumeroFecha());
        if (partido.getEquipoLocal() != null) {
            resp.setEquipoLocalId(partido.getEquipoLocal().getId());
            resp.setEquipoLocalNombre(partido.getEquipoLocal().getNombre());
        }
        if (partido.getEquipoVisitante() != null) {
            resp.setEquipoVisitanteId(partido.getEquipoVisitante().getId());
            resp.setEquipoVisitanteNombre(partido.getEquipoVisitante().getNombre());
        }
        if (partido.getCancha() != null) {
            resp.setCanchaId(partido.getCancha().getId());
            resp.setCanchaNombre(partido.getCancha().getNombre());
        }
        if (partido.getArbitro() != null) {
            resp.setArbitroId(partido.getArbitro().getId());
            resp.setArbitroNombre(partido.getArbitro().getNombre() + " " + partido.getArbitro().getApellido());
        }
        resp.setFechaHoraInicio(partido.getFechaHoraInicio());
        resp.setEstado(partido.getEstado());
        return resp;
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
}
