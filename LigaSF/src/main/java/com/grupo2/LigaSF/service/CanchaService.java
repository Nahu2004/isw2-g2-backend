package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.CanchaRequest;
import com.grupo2.LigaSF.dto.response.CanchaResponse;

import java.util.List;

public interface CanchaService {
    CanchaResponse crear(CanchaRequest request);
    CanchaResponse obtenerPorId(Integer id);
    List<CanchaResponse> listar(boolean soloActivos);
    CanchaResponse desactivar(Integer id);
    CanchaResponse reactivar(Integer id);
}
