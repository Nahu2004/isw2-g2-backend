package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.EquipoRequest;
import com.grupo2.LigaSF.dto.response.EquipoResponse;

import java.util.List;

public interface EquipoService {

    EquipoResponse crear(EquipoRequest request);

    EquipoResponse actualizar(Integer id, EquipoRequest request);

    EquipoResponse desactivar(Integer id);

    EquipoResponse reactivar(Integer id);

    EquipoResponse obtenerPorId(Integer id);

    List<EquipoResponse> listar(Integer torneoId, boolean soloActivos);
}
