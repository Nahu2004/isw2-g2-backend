package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.entity.EventoPartido;
import com.grupo2.LigaSF.entity.Jugador;
import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.entity.enums.TipoEvento;
import com.grupo2.LigaSF.repository.EventoPartidoRepository;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.impl.SancionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SancionServiceTest {

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private EventoPartidoRepository eventoPartidoRepository;

    @InjectMocks
    private SancionServiceImpl sancionService;

    @Test
    @DisplayName("Fecha 1 no debe tener suspendidos de fechas previas")
    void fecha1SinSuspendidos() {
        assertFalse(sancionService.estaJugadorSuspendido(1, 1, 1));
        assertTrue(sancionService.obtenerJugadoresSuspendidos(1, 1).isEmpty());
    }

    @Test
    @DisplayName("Jugador con ROJA en fecha N queda suspendido para fecha N+1")
    void jugadorConRojaSuspendidoEnFechaSiguiente() {
        Partido p = new Partido();
        p.setId(10);
        p.setEstado(EstadoPartido.FINALIZADO);

        when(partidoRepository.findByTorneoIdAndNumeroFecha(1, 1)).thenReturn(List.of(p));

        Jugador jugador = new Jugador();
        jugador.setId(50);

        EventoPartido eventoRoja = new EventoPartido(p, jugador, TipoEvento.ROJA, 44);
        when(eventoPartidoRepository.findByPartidoId(10)).thenReturn(List.of(eventoRoja));

        boolean suspendido = sancionService.estaJugadorSuspendido(50, 1, 2);
        assertTrue(suspendido);

        Set<Integer> suspendidos = sancionService.obtenerJugadoresSuspendidos(1, 2);
        assertTrue(suspendidos.contains(50));
    }

    @Test
    @DisplayName("Jugador con 2 AMARILLAS en el mismo partido de fecha N queda suspendido para fecha N+1")
    void jugadorConDosAmarillasSuspendidoEnFechaSiguiente() {
        Partido p = new Partido();
        p.setId(10);
        p.setEstado(EstadoPartido.FINALIZADO);

        when(partidoRepository.findByTorneoIdAndNumeroFecha(1, 1)).thenReturn(List.of(p));

        Jugador jugador = new Jugador();
        jugador.setId(50);

        EventoPartido am1 = new EventoPartido(p, jugador, TipoEvento.AMARILLA, 20);
        EventoPartido am2 = new EventoPartido(p, jugador, TipoEvento.AMARILLA, 70);
        when(eventoPartidoRepository.findByPartidoId(10)).thenReturn(List.of(am1, am2));

        boolean suspendido = sancionService.estaJugadorSuspendido(50, 1, 2);
        assertTrue(suspendido);
    }

    @Test
    @DisplayName("Jugador con 1 sola AMARILLA en fecha N NO queda suspendido para fecha N+1")
    void jugadorConUnaAmarillaNoSuspendido() {
        Partido p = new Partido();
        p.setId(10);
        p.setEstado(EstadoPartido.FINALIZADO);

        when(partidoRepository.findByTorneoIdAndNumeroFecha(1, 1)).thenReturn(List.of(p));

        Jugador jugador = new Jugador();
        jugador.setId(50);

        EventoPartido am1 = new EventoPartido(p, jugador, TipoEvento.AMARILLA, 20);
        when(eventoPartidoRepository.findByPartidoId(10)).thenReturn(List.of(am1));

        boolean suspendido = sancionService.estaJugadorSuspendido(50, 1, 2);
        assertFalse(suspendido);
    }
}
