package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.TorneoRequest;
import com.grupo2.LigaSF.dto.response.TorneoResponse;

import java.util.List;

public interface TorneoService {
    TorneoResponse crear(TorneoRequest request);
    TorneoResponse obtenerPorId(Integer id);
    List<TorneoResponse> listar(boolean soloActivos);
}
