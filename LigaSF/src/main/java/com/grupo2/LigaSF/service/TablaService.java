package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.response.PosicionResponse;

import java.util.List;

public interface TablaService {

    List<PosicionResponse> calcular(Integer torneoId);
}
