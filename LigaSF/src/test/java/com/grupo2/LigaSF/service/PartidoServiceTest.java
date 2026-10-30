package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.PartidoRequest;
import com.grupo2.LigaSF.dto.response.PartidoResponse;
import com.grupo2.LigaSF.entity.*;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.repository.*;
import com.grupo2.LigaSF.service.impl.PartidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartidoServiceTest {

    @Mock
    private PartidoRepository partidoRepository;
    @Mock
    private TorneoRepository torneoRepository;
    @Mock
    private EquipoRepository equipoRepository;
    @Mock
    private CanchaRepository canchaRepository;
    @Mock
    private ArbitroRepository arbitroRepository;
    @Mock
    private EventoPartidoRepository eventoPartidoRepository;

    @InjectMocks
    private PartidoServiceImpl partidoService;

    private Torneo torneo;
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private Cancha cancha;
    private Arbitro arbitro;
    private LocalDateTime fechaInicio;

    @BeforeEach
    void setUp() {
        torneo = new Torneo("Apertura 2026", 2026, true);
        torneo.setId(1);

        equipoLocal = new Equipo(torneo, "Colón FC", "Centenario", true);
        equipoLocal.setId(10);

        equipoVisitante = new Equipo(torneo, "Unión FC", "La Florida", true);
        equipoVisitante.setId(20);

        cancha = new Cancha("Cancha Municipal Norte", "Av. Blas Parera 7400", true);
        cancha.setId(1);

        arbitro = new Arbitro("Marcelo", "Quiroga", "28111222", true);
        arbitro.setId(1);

        fechaInicio = LocalDateTime.of(2026, 11, 1, 16, 0);
    }

    @Test
    @DisplayName("Debe programar un partido exitosamente en estado PROGRAMADO")
    void programarPartidoExitoso() {
        PartidoRequest request = new PartidoRequest(1, 1, 10, 20, 1, 1, fechaInicio);

        when(torneoRepository.findById(1)).thenReturn(Optional.of(torneo));
        when(equipoRepository.findById(10)).thenReturn(Optional.of(equipoLocal));
        when(equipoRepository.findById(20)).thenReturn(Optional.of(equipoVisitante));
        when(canchaRepository.findById(1)).thenReturn(Optional.of(cancha));
        when(arbitroRepository.findById(1)).thenReturn(Optional.of(arbitro));

        when(partidoRepository.findPartidosEquipoEnFecha(1, 1, 10)).thenReturn(Collections.emptyList());
        when(partidoRepository.findPartidosEquipoEnFecha(1, 1, 20)).thenReturn(Collections.emptyList());
        when(partidoRepository.findByCanchaIdAndEstadoNot(1, EstadoPartido.SUSPENDIDO)).thenReturn(Collections.emptyList());
        when(partidoRepository.findByArbitroIdAndEstadoNot(1, EstadoPartido.SUSPENDIDO)).thenReturn(Collections.emptyList());

        Partido partidoGuardado = new Partido(torneo, 1, equipoLocal, equipoVisitante, cancha, arbitro, fechaInicio, EstadoPartido.PROGRAMADO);
        partidoGuardado.setId(100);
        when(partidoRepository.save(any(Partido.class))).thenReturn(partidoGuardado);
        when(eventoPartidoRepository.findByPartidoId(100)).thenReturn(Collections.emptyList());

        PartidoResponse response = partidoService.programar(request);

        assertNotNull(response);
        assertEquals(100, response.getId());
        assertEquals(EstadoPartido.PROGRAMADO, response.getEstado());
        assertEquals("Colón FC", response.getEquipoLocalNombre());
        assertEquals("Unión FC", response.getEquipoVisitanteNombre());
        verify(partidoRepository, times(1)).save(any(Partido.class));
    }

    @Test
    @DisplayName("Debe fallar si local y visitante son el mismo equipo")
    void programarMismoEquipo() {
        PartidoRequest request = new PartidoRequest(1, 1, 10, 10, 1, 1, fechaInicio);

        assertThrows(BadRequestException.class, () -> partidoService.programar(request));
        verify(partidoRepository, never()).save(any(Partido.class));
    }

    @Test
    @DisplayName("Debe fallar por solapamiento de cancha a menos de 2 horas (RN-01)")
    void solapamientoCanchaRN01() {
        PartidoRequest request = new PartidoRequest(1, 1, 10, 20, 1, 1, fechaInicio); // 16:00

        when(torneoRepository.findById(1)).thenReturn(Optional.of(torneo));
        when(equipoRepository.findById(10)).thenReturn(Optional.of(equipoLocal));
        when(equipoRepository.findById(20)).thenReturn(Optional.of(equipoVisitante));
        when(canchaRepository.findById(1)).thenReturn(Optional.of(cancha));
        when(arbitroRepository.findById(1)).thenReturn(Optional.of(arbitro));

        // Otro partido en la misma cancha a las 17:00 (a menos de 2 horas de las 16:00)
        Partido partidoConflicto = new Partido(torneo, 1, new Equipo(), new Equipo(), cancha, new Arbitro(),
                LocalDateTime.of(2026, 11, 1, 17, 0), EstadoPartido.PROGRAMADO);
        partidoConflicto.setId(99);

        when(partidoRepository.findByCanchaIdAndEstadoNot(1, EstadoPartido.SUSPENDIDO))
                .thenReturn(List.of(partidoConflicto));

        ConflictException ex = assertThrows(ConflictException.class, () -> partidoService.programar(request));
        assertTrue(ex.getMessage().contains("RN-01"));
    }

    @Test
    @DisplayName("Debe permitir si la cancha está separada por exactamente 2 horas (RN-01)")
    void canchaValidaSeparadaExactamenteDosHoras() {
        PartidoRequest request = new PartidoRequest(1, 1, 10, 20, 1, 1, fechaInicio); // 16:00

        when(torneoRepository.findById(1)).thenReturn(Optional.of(torneo));
        when(equipoRepository.findById(10)).thenReturn(Optional.of(equipoLocal));
        when(equipoRepository.findById(20)).thenReturn(Optional.of(equipoVisitante));
        when(canchaRepository.findById(1)).thenReturn(Optional.of(cancha));
        when(arbitroRepository.findById(1)).thenReturn(Optional.of(arbitro));

        // Otro partido en la misma cancha a las 18:00 (exactamente 2 horas, válido según RN-01)
        Partido partidoValido = new Partido(torneo, 1, new Equipo(), new Equipo(), cancha, new Arbitro(),
                LocalDateTime.of(2026, 11, 1, 18, 0), EstadoPartido.PROGRAMADO);
        partidoValido.setId(99);

        when(partidoRepository.findPartidosEquipoEnFecha(1, 1, 10)).thenReturn(Collections.emptyList());
        when(partidoRepository.findPartidosEquipoEnFecha(1, 1, 20)).thenReturn(Collections.emptyList());
        when(partidoRepository.findByCanchaIdAndEstadoNot(1, EstadoPartido.SUSPENDIDO))
                .thenReturn(List.of(partidoValido));
        when(partidoRepository.findByArbitroIdAndEstadoNot(1, EstadoPartido.SUSPENDIDO))
                .thenReturn(Collections.emptyList());

        Partido partidoGuardado = new Partido(torneo, 1, equipoLocal, equipoVisitante, cancha, arbitro, fechaInicio, EstadoPartido.PROGRAMADO);
        partidoGuardado.setId(100);
        when(partidoRepository.save(any(Partido.class))).thenReturn(partidoGuardado);
        when(eventoPartidoRepository.findByPartidoId(100)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> partidoService.programar(request));
    }

    @Test
    @DisplayName("Debe fallar si un equipo ya juega en esa fecha")
    void equipoJuegaDosVecesMismaFecha() {
        PartidoRequest request = new PartidoRequest(1, 1, 10, 20, 1, 1, fechaInicio);

        when(torneoRepository.findById(1)).thenReturn(Optional.of(torneo));
        when(equipoRepository.findById(10)).thenReturn(Optional.of(equipoLocal));
        when(equipoRepository.findById(20)).thenReturn(Optional.of(equipoVisitante));
        when(canchaRepository.findById(1)).thenReturn(Optional.of(cancha));
        when(arbitroRepository.findById(1)).thenReturn(Optional.of(arbitro));

        Partido partidoPrevio = new Partido();
        partidoPrevio.setId(50);
        partidoPrevio.setEstado(EstadoPartido.PROGRAMADO);

        when(partidoRepository.findPartidosEquipoEnFecha(1, 1, 10)).thenReturn(List.of(partidoPrevio));

        assertThrows(ConflictException.class, () -> partidoService.programar(request));
    }

    @Test
    @DisplayName("No debe permitir borrar un partido que no esté en estado PROGRAMADO")
    void eliminarPartidoNoProgramadoFalla() {
        Partido partido = new Partido();
        partido.setId(1);
        partido.setEstado(EstadoPartido.FINALIZADO);

        when(partidoRepository.findById(1)).thenReturn(Optional.of(partido));

        assertThrows(BadRequestException.class, () -> partidoService.eliminar(1));
        verify(partidoRepository, never()).delete(any(Partido.class));
    }
}
