package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.PartidoRequest;
import com.grupo2.LigaSF.dto.response.PartidoResponse;

import java.util.List;

public interface PartidoService {

    PartidoResponse programar(PartidoRequest request);

    PartidoResponse actualizar(Integer id, PartidoRequest request);

    void eliminar(Integer id);

    PartidoResponse obtenerPorId(Integer id);

    List<PartidoResponse> listar(Integer torneoId, Integer numeroFecha);
}
