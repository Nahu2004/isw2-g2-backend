package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.response.SuspendidoResponse;

import java.util.List;

public interface SuspendidoService {

    List<SuspendidoResponse> listarPorFecha(Integer torneoId, Integer numeroFecha);
}
