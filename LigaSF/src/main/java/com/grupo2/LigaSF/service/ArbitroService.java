package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.ArbitroRequest;
import com.grupo2.LigaSF.dto.response.ArbitroResponse;

import java.util.List;

public interface ArbitroService {
    ArbitroResponse crear(ArbitroRequest request);
    ArbitroResponse obtenerPorId(Integer id);
    List<ArbitroResponse> listar(boolean soloActivos);
    ArbitroResponse desactivar(Integer id);
    ArbitroResponse reactivar(Integer id);
}
