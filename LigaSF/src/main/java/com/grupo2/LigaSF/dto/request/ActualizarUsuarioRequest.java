package com.grupo2.LigaSF.dto.request;

import jakarta.validation.constraints.Size;

public class ActualizarUsuarioRequest {
    @Size(max = 100, message = "El nombre completo no puede superar 100 caracteres")
    private String nombreCompleto;

    private String rol; // "ADMIN" o "DELEGADO", puede ser null si no se cambia

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password; // null si no se cambia

    // getters y setters
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
