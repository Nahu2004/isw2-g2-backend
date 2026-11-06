package com.grupo2.LigaSF.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioRequest {
    @NotBlank(message = "El username es obligatorio")
    @Size(max = 40, message = "El username no puede superar 40 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 100, message = "El nombre completo no puede superar 100 caracteres")
    private String nombreCompleto;

    @NotNull(message = "El rol es obligatorio")
    private String rol; // "ADMIN" o "DELEGADO"

    // getters y setters para todos los campos
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
