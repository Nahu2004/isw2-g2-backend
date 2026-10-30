package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.JugadorRequest;
import com.grupo2.LigaSF.dto.response.JugadorResponse;

import java.util.List;

public interface JugadorService {

    JugadorResponse crear(JugadorRequest request);

    JugadorResponse actualizar(Integer id, JugadorRequest request);

    JugadorResponse desactivar(Integer id);

    JugadorResponse reactivar(Integer id);

    JugadorResponse obtenerPorId(Integer id);

    List<JugadorResponse> listar(Integer equipoId, boolean soloActivos);
}
