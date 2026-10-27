package com.grupo2.LigaSF.dto.response;

public class LoginResponse {

    private String token;
    private String tipo;
    private String username;
    private String nombreCompleto;
    private String rol;

    public LoginResponse() {
        this.tipo = "Bearer";
    }

    public LoginResponse(String token, String username, String nombreCompleto, String rol) {
        this.token = token;
        this.tipo = "Bearer";
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
