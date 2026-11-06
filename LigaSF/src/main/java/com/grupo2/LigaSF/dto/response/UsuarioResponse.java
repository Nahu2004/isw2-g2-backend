package com.grupo2.LigaSF.dto.response;

import java.time.LocalDateTime;

public class UsuarioResponse {
    private Integer id;
    private String username;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaAlta;

    public UsuarioResponse() {}

    public UsuarioResponse(Integer id, String username, String nombreCompleto, String rol, Boolean activo, LocalDateTime fechaAlta) {
        this.id = id;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = activo;
        this.fechaAlta = fechaAlta;
    }

    // getters y setters para todos
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDateTime fechaAlta) { this.fechaAlta = fechaAlta; }
}
