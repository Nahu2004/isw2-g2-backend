package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.EventoPartidoRequest;
import com.grupo2.LigaSF.dto.response.EventoPartidoResponse;
import com.grupo2.LigaSF.entity.*;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.entity.enums.TipoEvento;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.repository.EventoPartidoRepository;
import com.grupo2.LigaSF.repository.JugadorRepository;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.impl.EventoPartidoServiceImpl;
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
class EventoPartidoServiceTest {

    @Mock
    private EventoPartidoRepository eventoPartidoRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private SancionService sancionService;

    @InjectMocks
    private EventoPartidoServiceImpl eventoPartidoService;

    private Torneo torneo;
    private Equipo local;
    private Equipo visitante;
    private Equipo otroEquipo;
    private Partido partido;
    private Jugador jugadorLocal;
    private Jugador jugadorVisitante;
    private Jugador jugadorAjeno;

    @BeforeEach
    void setUp() {
        torneo = new Torneo("Apertura 2026", 2026, true);
        torneo.setId(1);

        local = new Equipo(torneo, "Local FC", "Norte", true);
        local.setId(1);

        visitante = new Equipo(torneo, "Visita FC", "Sur", true);
        visitante.setId(2);

        otroEquipo = new Equipo(torneo, "Otro FC", "Este", true);
        otroEquipo.setId(3);

        Cancha cancha = new Cancha("Cancha 1", "Calle 1", true);
        cancha.setId(1);
        Arbitro arbitro = new Arbitro("Juan", "Pérez", "12345678", true);
        arbitro.setId(1);

        partido = new Partido(torneo, 2, local, visitante, cancha, arbitro, LocalDateTime.now(), EstadoPartido.EN_JUEGO);
        partido.setId(10);

        jugadorLocal = new Jugador(local, "Lionel", "Messi", "11111111", 10, true);
        jugadorLocal.setId(101);

        jugadorVisitante = new Jugador(visitante, "Cristiano", "Ronaldo", "22222222", 7, true);
        jugadorVisitante.setId(102);

        jugadorAjeno = new Jugador(otroEquipo, "Neymar", "Junior", "33333333", 11, true);
        jugadorAjeno.setId(103);
    }

    @Test
    @DisplayName("Debe registrar gol válidamente para jugador local cuando partido está EN_JUEGO")
    void registrarGolExitoso() {
        EventoPartidoRequest request = new EventoPartidoRequest(101, TipoEvento.GOL, 25);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));
        when(jugadorRepository.findById(101)).thenReturn(Optional.of(jugadorLocal));
        when(sancionService.estaJugadorSuspendido(101, 1, 2)).thenReturn(false);

        EventoPartido eventoGuardado = new EventoPartido(partido, jugadorLocal, TipoEvento.GOL, 25);
        eventoGuardado.setId(1);
        when(eventoPartidoRepository.save(any(EventoPartido.class))).thenReturn(eventoGuardado);

        EventoPartidoResponse response = eventoPartidoService.registrarEvento(10, request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(TipoEvento.GOL, response.getTipo());
        assertEquals(25, response.getMinuto());
        assertEquals(101, response.getJugadorId());
        verify(eventoPartidoRepository, times(1)).save(any(EventoPartido.class));
    }

    @Test
    @DisplayName("Debe fallar al registrar evento si el partido NO está EN_JUEGO (ej: PROGRAMADO)")
    void registrarEventoPartidoNoEnJuego() {
        partido.setEstado(EstadoPartido.PROGRAMADO);
        EventoPartidoRequest request = new EventoPartidoRequest(101, TipoEvento.GOL, 25);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> eventoPartidoService.registrarEvento(10, request));
        assertTrue(ex.getMessage().contains("EN_JUEGO"));
        verify(eventoPartidoRepository, never()).save(any(EventoPartido.class));
    }

    @Test
    @DisplayName("Debe fallar al registrar evento si el jugador está inactivo")
    void registrarEventoJugadorInactivo() {
        jugadorLocal.setActivo(false);
        EventoPartidoRequest request = new EventoPartidoRequest(101, TipoEvento.AMARILLA, 15);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));
        when(jugadorRepository.findById(101)).thenReturn(Optional.of(jugadorLocal));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> eventoPartidoService.registrarEvento(10, request));
        assertTrue(ex.getMessage().contains("inactivo"));
    }

    @Test
    @DisplayName("Debe fallar si el jugador no pertenece a ninguno de los equipos del partido")
    void registrarEventoJugadorEquipoAjeno() {
        EventoPartidoRequest request = new EventoPartidoRequest(103, TipoEvento.ROJA, 40);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));
        when(jugadorRepository.findById(103)).thenReturn(Optional.of(jugadorAjeno));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> eventoPartidoService.registrarEvento(10, request));
        assertTrue(ex.getMessage().contains("no pertenece a ninguno de los dos equipos"));
    }

    @Test
    @DisplayName("Debe fallar si el jugador está suspendido para esa fecha")
    void registrarEventoJugadorSuspendido() {
        EventoPartidoRequest request = new EventoPartidoRequest(101, TipoEvento.GOL, 50);

        when(partidoRepository.findById(10)).thenReturn(Optional.of(partido));
        when(jugadorRepository.findById(101)).thenReturn(Optional.of(jugadorLocal));
        when(sancionService.estaJugadorSuspendido(101, 1, 2)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> eventoPartidoService.registrarEvento(10, request));
        assertTrue(ex.getMessage().contains("suspendido"));
    }

    @Test
    @DisplayName("Debe permitir corregir (eliminar) evento mientras el partido sigue EN_JUEGO")
    void eliminarEventoExitoso() {
        EventoPartido evento = new EventoPartido(partido, jugadorLocal, TipoEvento.AMARILLA, 30);
        evento.setId(5);

        when(eventoPartidoRepository.findById(5)).thenReturn(Optional.of(evento));

        eventoPartidoService.eliminarEvento(5);

        verify(eventoPartidoRepository, times(1)).delete(evento);
    }

    @Test
    @DisplayName("Debe fallar al corregir (eliminar) evento si el partido ya está FINALIZADO")
    void eliminarEventoPartidoFinalizadoFalla() {
        partido.setEstado(EstadoPartido.FINALIZADO);
        EventoPartido evento = new EventoPartido(partido, jugadorLocal, TipoEvento.AMARILLA, 30);
        evento.setId(5);

        when(eventoPartidoRepository.findById(5)).thenReturn(Optional.of(evento));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> eventoPartidoService.eliminarEvento(5));
        assertTrue(ex.getMessage().contains("EN_JUEGO"));
        verify(eventoPartidoRepository, never()).delete(any(EventoPartido.class));
    }
}
