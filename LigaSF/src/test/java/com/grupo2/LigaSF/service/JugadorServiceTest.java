package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.JugadorRequest;
import com.grupo2.LigaSF.dto.response.JugadorResponse;
import com.grupo2.LigaSF.entity.Equipo;
import com.grupo2.LigaSF.entity.Jugador;
import com.grupo2.LigaSF.entity.Torneo;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.EquipoRepository;
import com.grupo2.LigaSF.repository.JugadorRepository;
import com.grupo2.LigaSF.service.impl.JugadorServiceImpl;
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
class JugadorServiceTest {

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private EquipoRepository equipoRepository;

    @InjectMocks
    private JugadorServiceImpl jugadorService;

    private Equipo equipo;

    @BeforeEach
    void setUp() {
        Torneo torneo = new Torneo("Apertura 2026", 2026, true);
        torneo.setId(1);
        equipo = new Equipo(torneo, "Los Andes", "Barrio Norte", true);
        equipo.setId(10);
    }

    @Test
    @DisplayName("Debe crear un jugador correctamente")
    void crearJugadorExitoso() {
        JugadorRequest request = new JugadorRequest(10, "Juan", "Pérez", "40123456", 10);

        when(equipoRepository.findById(10)).thenReturn(Optional.of(equipo));
        when(jugadorRepository.existsByDni("40123456")).thenReturn(false);

        Jugador jugadorGuardado = new Jugador(equipo, "Juan", "Pérez", "40123456", 10, true);
        jugadorGuardado.setId(1);
        when(jugadorRepository.save(any(Jugador.class))).thenReturn(jugadorGuardado);

        JugadorResponse response = jugadorService.crear(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Juan", response.getNombre());
        assertEquals("Pérez", response.getApellido());
        assertEquals("40123456", response.getDni());
        assertEquals(10, response.getNumeroCamiseta());
        assertTrue(response.getActivo());
        verify(jugadorRepository, times(1)).save(any(Jugador.class));
    }

    @Test
    @DisplayName("Debe lanzar ConflictException si el DNI ya existe")
    void crearJugadorDniDuplicado() {
        JugadorRequest request = new JugadorRequest(10, "Juan", "Pérez", "40123456", 10);

        when(equipoRepository.findById(10)).thenReturn(Optional.of(equipo));
        when(jugadorRepository.existsByDni("40123456")).thenReturn(true);

        assertThrows(ConflictException.class, () -> jugadorService.crear(request));
        verify(jugadorRepository, never()).save(any(Jugador.class));
    }

    @Test
    @DisplayName("Debe lanzar BadRequestException si el equipo está inactivo")
    void crearJugadorEquipoInactivo() {
        equipo.setActivo(false);
        JugadorRequest request = new JugadorRequest(10, "Juan", "Pérez", "40123456", 10);

        when(equipoRepository.findById(10)).thenReturn(Optional.of(equipo));

        assertThrows(BadRequestException.class, () -> jugadorService.crear(request));
    }

    @Test
    @DisplayName("Debe lanzar BadRequestException si intenta cambiar de equipo")
    void actualizarJugadorCambioDeEquipoNoPermitido() {
        Jugador jugador = new Jugador(equipo, "Juan", "Pérez", "40123456", 10, true);
        jugador.setId(1);

        JugadorRequest request = new JugadorRequest(20, "Juan", "Pérez", "40123456", 10);

        when(jugadorRepository.findById(1)).thenReturn(Optional.of(jugador));

        assertThrows(BadRequestException.class, () -> jugadorService.actualizar(1, request));
    }

    @Test
    @DisplayName("Debe realizar la baja lógica desactivando el jugador (activo = false)")
    void desactivarJugadorBajaLogica() {
        Jugador jugador = new Jugador(equipo, "Juan", "Pérez", "40123456", 10, true);
        jugador.setId(1);

        when(jugadorRepository.findById(1)).thenReturn(Optional.of(jugador));
        when(jugadorRepository.save(any(Jugador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JugadorResponse response = jugadorService.desactivar(1);

        assertNotNull(response);
        assertFalse(response.getActivo());
        assertFalse(jugador.getActivo());
        verify(jugadorRepository, times(1)).save(jugador);
    }
}
