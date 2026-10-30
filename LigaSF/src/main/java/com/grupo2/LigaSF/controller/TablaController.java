package com.grupo2.LigaSF.controller;

import com.grupo2.LigaSF.dto.response.PosicionResponse;
import com.grupo2.LigaSF.service.TablaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tabla")
public class TablaController {

    private final TablaService tablaService;

    public TablaController(TablaService tablaService) {
        this.tablaService = tablaService;
    }

    @GetMapping
    public ResponseEntity<List<PosicionResponse>> obtener(@RequestParam Integer torneoId) {
        return ResponseEntity.ok(tablaService.calcular(torneoId));
    }
}
