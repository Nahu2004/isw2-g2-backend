package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.CambiarEstadoPartidoRequest;
import com.grupo2.LigaSF.dto.response.PartidoResponse;

public interface PartidoService {
    PartidoResponse cambiarEstado(Integer id, CambiarEstadoPartidoRequest request);
}
