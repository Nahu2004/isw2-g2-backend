package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.CambiarEstadoPartidoRequest;
import com.grupo2.LigaSF.dto.response.PartidoResponse;
import com.grupo2.LigaSF.entity.Arbitro;
import com.grupo2.LigaSF.entity.Cancha;
import com.grupo2.LigaSF.entity.Equipo;
import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.Torneo;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.impl.PartidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartidoServiceTest {

    @Mock
    private PartidoRepository partidoRepository;

    @InjectMocks
    private PartidoServiceImpl partidoService;

    private Partido partido;

    @BeforeEach
    void setUp() {
        Torneo torneo = new Torneo("Apertura 2026", 2026, true);
        torneo.setId(1);

        Equipo local = new Equipo(torneo, "Local FC", "Norte", true);
        local.setId(1);
        Equipo visitante = new Equipo(torneo, "Visita FC", "Sur", true);
        visitante.setId(2);

        Cancha cancha = new Cancha("Cancha 1", "Calle 1", true);
        cancha.setId(1);
        Arbitro arbitro = new Arbitro("Juan", "Pérez", "12345678", true);
        arbitro.setId(1);

        partido = new Partido(torneo, 1, local, visitante, cancha, arbitro, LocalDateTime.now(), EstadoPartido.PROGRAMADO);
        partido.setId(10);
    }

    @Test
    @DisplayName("Debe cambiar estado de PROGRAMADO a EN_JUEGO exitosamente")
    void cambiarEstadoProgramadoAEnJuego() {
        CambiarEstadoPartidoRequest request = new CambiarEstadoPartidoRequest(EstadoPartido.EN_JUEGO);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartidoResponse response = partidoService.cambiarEstado(10, request);

        assertNotNull(response);
        assertEquals(EstadoPartido.EN_JUEGO, response.getEstado());
        assertEquals(EstadoPartido.EN_JUEGO, partido.getEstado());
        verify(partidoRepository, times(1)).save(partido);
    }

    @Test
    @DisplayName("Debe cambiar estado de PROGRAMADO a SUSPENDIDO exitosamente")
    void cambiarEstadoProgramadoASuspendido() {
        CambiarEstadoPartidoRequest request = new CambiarEstadoPartidoRequest(EstadoPartido.SUSPENDIDO);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartidoResponse response = partidoService.cambiarEstado(10, request);

        assertNotNull(response);
        assertEquals(EstadoPartido.SUSPENDIDO, response.getEstado());
        assertEquals(EstadoPartido.SUSPENDIDO, partido.getEstado());
    }

    @Test
    @DisplayName("Debe cambiar estado de EN_JUEGO a FINALIZADO exitosamente")
    void cambiarEstadoEnJuegoAFinalizado() {
        partido.setEstado(EstadoPartido.EN_JUEGO);
        CambiarEstadoPartidoRequest request = new CambiarEstadoPartidoRequest(EstadoPartido.FINALIZADO);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartidoResponse response = partidoService.cambiarEstado(10, request);

        assertNotNull(response);
        assertEquals(EstadoPartido.FINALIZADO, response.getEstado());
    }

    @Test
    @DisplayName("Debe fallar al intentar cambiar directamente de PROGRAMADO a FINALIZADO")
    void cambiarEstadoProgramadoAFinalizadoInvalido() {
        CambiarEstadoPartidoRequest request = new CambiarEstadoPartidoRequest(EstadoPartido.FINALIZADO);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> partidoService.cambiarEstado(10, request));
        assertTrue(ex.getMessage().contains("Transición de estado no válida"));
        verify(partidoRepository, never()).save(any(Partido.class));
    }

    @Test
    @DisplayName("Debe fallar si el partido ya está FINALIZADO (es irreversible)")
    void cambiarEstadoPartidoFinalizadoIrreversible() {
        partido.setEstado(EstadoPartido.FINALIZADO);
        CambiarEstadoPartidoRequest request = new CambiarEstadoPartidoRequest(EstadoPartido.EN_JUEGO);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> partidoService.cambiarEstado(10, request));
        assertTrue(ex.getMessage().contains("No se puede cambiar el estado de un partido que ya se encuentra FINALIZADO"));
        verify(partidoRepository, never()).save(any(Partido.class));
    }

    @Test
    @DisplayName("Debe fallar si el partido ya está SUSPENDIDO (es irreversible)")
    void cambiarEstadoPartidoSuspendidoIrreversible() {
        partido.setEstado(EstadoPartido.SUSPENDIDO);
        CambiarEstadoPartidoRequest request = new CambiarEstadoPartidoRequest(EstadoPartido.EN_JUEGO);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> partidoService.cambiarEstado(10, request));
        assertTrue(ex.getMessage().contains("No se puede cambiar el estado de un partido que ya se encuentra SUSPENDIDO"));
        verify(partidoRepository, never()).save(any(Partido.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el partido no existe")
    void cambiarEstadoPartidoInexistente() {
        CambiarEstadoPartidoRequest request = new CambiarEstadoPartidoRequest(EstadoPartido.EN_JUEGO);

        when(partidoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partidoService.cambiarEstado(999, request));
    }
}
