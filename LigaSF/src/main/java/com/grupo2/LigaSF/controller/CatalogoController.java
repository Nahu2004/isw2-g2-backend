package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.response.CatalogoResponse;
import com.grupo2.LigaSF.service.CatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    public ResponseEntity<CatalogoResponse> obtenerCatalogo() {
        return ResponseEntity.ok(catalogoService.obtenerCatalogo());
    }
}
