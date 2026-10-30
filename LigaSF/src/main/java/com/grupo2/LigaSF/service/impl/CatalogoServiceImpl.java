package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.response.CatalogoResponse;
import com.grupo2.LigaSF.service.ArbitroService;
import com.grupo2.LigaSF.service.CanchaService;
import com.grupo2.LigaSF.service.CatalogoService;
import com.grupo2.LigaSF.service.TorneoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogoServiceImpl implements CatalogoService {

    private final TorneoService torneoService;
    private final CanchaService canchaService;
    private final ArbitroService arbitroService;

    public CatalogoServiceImpl(TorneoService torneoService, CanchaService canchaService, ArbitroService arbitroService) {
        this.torneoService = torneoService;
        this.canchaService = canchaService;
        this.arbitroService = arbitroService;
    }

    @Override
    public CatalogoResponse obtenerCatalogo() {
        return new CatalogoResponse(
                torneoService.listar(true),
                canchaService.listar(true),
                arbitroService.listar(true)
        );
    }
}
