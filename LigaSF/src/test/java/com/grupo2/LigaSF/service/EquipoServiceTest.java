package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.EquipoRequest;
import com.grupo2.LigaSF.dto.response.EquipoResponse;
import com.grupo2.LigaSF.entity.Equipo;
import com.grupo2.LigaSF.entity.Torneo;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.EquipoRepository;
import com.grupo2.LigaSF.repository.TorneoRepository;
import com.grupo2.LigaSF.service.impl.EquipoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipoServiceTest {

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private TorneoRepository torneoRepository;

    @InjectMocks
    private EquipoServiceImpl equipoService;

    private Torneo torneo;

    @BeforeEach
    void setUp() {
        torneo = new Torneo("Apertura 2026", 2026, true);
        torneo.setId(1);
    }

    @Test
    @DisplayName("Debe crear un equipo correctamente")
    void crearEquipoExitoso() {
        EquipoRequest request = new EquipoRequest(1, "Los Andes", "Barrio Norte");

        when(torneoRepository.findById(1)).thenReturn(Optional.of(torneo));
        when(equipoRepository.existsByTorneoIdAndNombreIgnoreCase(1, "Los Andes")).thenReturn(false);

        Equipo equipoGuardado = new Equipo(torneo, "Los Andes", "Barrio Norte", true);
        equipoGuardado.setId(10);
        when(equipoRepository.save(any(Equipo.class))).thenReturn(equipoGuardado);

        EquipoResponse response = equipoService.crear(request);

        assertNotNull(response);
        assertEquals(10, response.getId());
        assertEquals("Los Andes", response.getNombre());
        assertEquals("Barrio Norte", response.getBarrio());
        assertTrue(response.getActivo());
        verify(equipoRepository, times(1)).save(any(Equipo.class));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el nombre ya existe en el torneo")
    void crearEquipoNombreDuplicado() {
        EquipoRequest request = new EquipoRequest(1, "Los Andes", "Barrio Norte");

        when(torneoRepository.findById(1)).thenReturn(Optional.of(torneo));
        when(equipoRepository.existsByTorneoIdAndNombreIgnoreCase(1, "Los Andes")).thenReturn(true);

        assertThrows(ConflictException.class, () -> equipoService.crear(request));
        verify(equipoRepository, never()).save(any(Equipo.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el torneo no existe")
    void crearEquipoTorneoInexistente() {
        EquipoRequest request = new EquipoRequest(99, "Los Andes", "Barrio Norte");

        when(torneoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> equipoService.crear(request));
    }

    @Test
    @DisplayName("Debe realizar la baja lógica desactivando el equipo (activo = false)")
    void desactivarEquipoBajaLogica() {
        Equipo equipo = new Equipo(torneo, "Los Andes", "Barrio Norte", true);
        equipo.setId(10);

        when(equipoRepository.findById(10)).thenReturn(Optional.of(equipo));
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EquipoResponse response = equipoService.desactivar(10);

        assertNotNull(response);
        assertFalse(response.getActivo());
        assertFalse(equipo.getActivo());
        verify(equipoRepository, times(1)).save(equipo);
    }

    @Test
    @DisplayName("Debe reactivar un equipo desactivado")
    void reactivarEquipo() {
        Equipo equipo = new Equipo(torneo, "Los Andes", "Barrio Norte", false);
        equipo.setId(10);

        when(equipoRepository.findById(10)).thenReturn(Optional.of(equipo));
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EquipoResponse response = equipoService.reactivar(10);

        assertNotNull(response);
        assertTrue(response.getActivo());
        assertTrue(equipo.getActivo());
    }
}
