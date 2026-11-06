package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.ActualizarUsuarioRequest;
import com.grupo2.LigaSF.dto.request.UsuarioRequest;
import com.grupo2.LigaSF.dto.response.UsuarioResponse;
import java.util.List;

public interface UsuarioService {
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse actualizar(Integer id, ActualizarUsuarioRequest request, String usernameAdmin);
    UsuarioResponse desactivar(Integer id, String usernameAdmin);
    UsuarioResponse reactivar(Integer id);
    UsuarioResponse obtenerPorId(Integer id);
    List<UsuarioResponse> listar();
}
