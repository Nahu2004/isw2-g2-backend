package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.ArbitroRequest;
import com.grupo2.LigaSF.dto.request.CanchaRequest;
import com.grupo2.LigaSF.dto.response.ArbitroResponse;
import com.grupo2.LigaSF.dto.response.CanchaResponse;
import com.grupo2.LigaSF.dto.response.CatalogoResponse;
import com.grupo2.LigaSF.dto.response.TorneoResponse;
import com.grupo2.LigaSF.entity.Arbitro;
import com.grupo2.LigaSF.entity.Cancha;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.repository.ArbitroRepository;
import com.grupo2.LigaSF.repository.CanchaRepository;
import com.grupo2.LigaSF.service.impl.ArbitroServiceImpl;
import com.grupo2.LigaSF.service.impl.CanchaServiceImpl;
import com.grupo2.LigaSF.service.impl.CatalogoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    @Mock
    private CanchaRepository canchaRepository;

    @Mock
    private ArbitroRepository arbitroRepository;

    @Mock
    private TorneoService torneoService;

    @Mock
    private CanchaService canchaService;

    @Mock
    private ArbitroService arbitroService;

    @InjectMocks
    private CanchaServiceImpl canchaServiceImpl;

    @InjectMocks
    private ArbitroServiceImpl arbitroServiceImpl;

    @Test
    @DisplayName("Debe crear una cancha correctamente")
    void crearCanchaExitoso() {
        CanchaRequest request = new CanchaRequest("Cancha 1", "Calle Falsa 123");
        when(canchaRepository.existsByNombreIgnoreCase("Cancha 1")).thenReturn(false);

        Cancha canchaGuardada = new Cancha("Cancha 1", "Calle Falsa 123", true);
        canchaGuardada.setId(1);
        when(canchaRepository.save(any(Cancha.class))).thenReturn(canchaGuardada);

        CanchaResponse response = canchaServiceImpl.crear(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Cancha 1", response.getNombre());
        assertTrue(response.getActivo());
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el nombre de cancha ya existe")
    void crearCanchaNombreDuplicado() {
        CanchaRequest request = new CanchaRequest("Cancha 1", "Calle Falsa 123");
        when(canchaRepository.existsByNombreIgnoreCase("Cancha 1")).thenReturn(true);

        assertThrows(ConflictException.class, () -> canchaServiceImpl.crear(request));
    }

    @Test
    @DisplayName("Debe crear un árbitro correctamente")
    void crearArbitroExitoso() {
        ArbitroRequest request = new ArbitroRequest("Carlos", "Gómez", "35123456");
        when(arbitroRepository.existsByDni("35123456")).thenReturn(false);

        Arbitro arbitroGuardado = new Arbitro("Carlos", "Gómez", "35123456", true);
        arbitroGuardado.setId(1);
        when(arbitroRepository.save(any(Arbitro.class))).thenReturn(arbitroGuardado);

        ArbitroResponse response = arbitroServiceImpl.crear(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Carlos", response.getNombre());
        assertEquals("35123456", response.getDni());
        assertTrue(response.getActivo());
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el DNI del árbitro ya existe")
    void crearArbitroDniDuplicado() {
        ArbitroRequest request = new ArbitroRequest("Carlos", "Gómez", "35123456");
        when(arbitroRepository.existsByDni("35123456")).thenReturn(true);

        assertThrows(ConflictException.class, () -> arbitroServiceImpl.crear(request));
    }

    @Test
    @DisplayName("Debe obtener el catálogo completo de torneos, canchas y árbitros activos")
    void obtenerCatalogoCompleto() {
        CatalogoServiceImpl catalogoService = new CatalogoServiceImpl(torneoService, canchaService, arbitroService);

        when(torneoService.listar(true)).thenReturn(List.of(new TorneoResponse(1, "Apertura 2026", 2026, true)));
        when(canchaService.listar(true)).thenReturn(List.of(new CanchaResponse(1, "Municipal Norte", "Dir", true)));
        when(arbitroService.listar(true)).thenReturn(List.of(new ArbitroResponse(1, "Marcelo", "Quiroga", "28111222", true)));

        CatalogoResponse response = catalogoService.obtenerCatalogo();

        assertNotNull(response);
        assertEquals(1, response.getTorneos().size());
        assertEquals(1, response.getCanchas().size());
        assertEquals(1, response.getArbitros().size());
    }
}
