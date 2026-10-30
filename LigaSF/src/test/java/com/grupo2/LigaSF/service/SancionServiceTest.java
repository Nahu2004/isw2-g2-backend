package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.response.SancionResponse;
import com.grupo2.LigaSF.entity.EventoPartido;
import com.grupo2.LigaSF.entity.Jugador;
import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.entity.enums.TipoEvento;
import com.grupo2.LigaSF.repository.EventoPartidoRepository;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.impl.SancionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Módulo 4 – Reglamento: Tests unitarios de SancionServiceImpl.
 *
 * <p>Valida RN-03 (cálculo de suspensiones por tarjetas) sin levantar
 * el contexto de Spring ni usar base de datos real.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Módulo 4 – Reglamento: Sanciones (RN-03)")
class SancionServiceTest {

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private EventoPartidoRepository eventoPartidoRepository;

    @InjectMocks
    private SancionServiceImpl sancionService;

    // ── Datos reutilizables ───────────────────────────────────────────────────
    private static final Integer TORNEO_ID  = 1;
    private static final Integer FECHA_N    = 2;  // fecha que se consulta
    private static final Integer FECHA_N1   = 1;  // fecha previa (N-1)

    private Partido partidoFinalizado;
    private Jugador jugador;

    @BeforeEach
    void setUp() {
        partidoFinalizado = new Partido();
        partidoFinalizado.setId(10);
        partidoFinalizado.setEstado(EstadoPartido.FINALIZADO);

        jugador = new Jugador();
        jugador.setId(50);
        jugador.setNombre("Lucas");
        jugador.setApellido("Martínez");
    }

    // =========================================================================
    // Casos base
    // =========================================================================

    @Test
    @DisplayName("RN-03 | Fecha 1 → sin suspendidos (no hay fecha previa)")
    void fecha1_sinSuspendidos() {
        // La fecha 1 nunca puede tener suspendidos porque no existe fecha 0
        assertFalse(sancionService.estaJugadorSuspendido(50, TORNEO_ID, 1));
        assertTrue(sancionService.obtenerJugadoresSuspendidos(TORNEO_ID, 1).isEmpty());
        assertTrue(sancionService.listarSancionados(TORNEO_ID, 1).isEmpty());
    }

    @Test
    @DisplayName("RN-03 | Partido PROGRAMADO (no finalizado) → no genera sanción")
    void partidoNOFinalizado_noGeneraSancion() {
        // given: el partido de la fecha previa NO está finalizado
        Partido partidoProgramado = new Partido();
        partidoProgramado.setId(10);
        partidoProgramado.setEstado(EstadoPartido.PROGRAMADO);

        when(partidoRepository.findByTorneoIdAndNumeroFecha(TORNEO_ID, FECHA_N1))
                .thenReturn(List.of(partidoProgramado));

        // when/then: no debe generar ninguna sanción
        List<SancionResponse> sanciones = sancionService.listarSancionados(TORNEO_ID, FECHA_N);
        assertTrue(sanciones.isEmpty(),
                "Un partido no finalizado no debe generar suspensiones");
    }

    // =========================================================================
    // Tarjeta roja
    // =========================================================================

    @Test
    @DisplayName("RN-03 | Tarjeta ROJA en fecha N → suspendido con motivo 'ROJA' en fecha N+1")
    void tarjetaRoja_suspende_conMotivoRoja() {
        // given
        when(partidoRepository.findByTorneoIdAndNumeroFecha(TORNEO_ID, FECHA_N1))
                .thenReturn(List.of(partidoFinalizado));

        EventoPartido roja = new EventoPartido(partidoFinalizado, jugador, TipoEvento.ROJA, 44);
        when(eventoPartidoRepository.findByPartidoId(10)).thenReturn(List.of(roja));

        // when
        List<SancionResponse> sanciones = sancionService.listarSancionados(TORNEO_ID, FECHA_N);

        // then
        assertEquals(1, sanciones.size());
        SancionResponse sancion = sanciones.get(0);
        assertEquals(50, sancion.getJugadorId());
        assertEquals("Lucas Martínez", sancion.getNombreJugador());
        assertEquals(FECHA_N, sancion.getFechaSuspendido());
        assertEquals("ROJA", sancion.getMotivo());
    }

    @Test
    @DisplayName("RN-03 | Tarjeta ROJA → estaJugadorSuspendido() devuelve true")
    void tarjetaRoja_estaJugadorSuspendido_devuelveTrue() {
        when(partidoRepository.findByTorneoIdAndNumeroFecha(TORNEO_ID, FECHA_N1))
                .thenReturn(List.of(partidoFinalizado));
        when(eventoPartidoRepository.findByPartidoId(10))
                .thenReturn(List.of(new EventoPartido(partidoFinalizado, jugador, TipoEvento.ROJA, 44)));

        assertTrue(sancionService.estaJugadorSuspendido(50, TORNEO_ID, FECHA_N));
    }

    // =========================================================================
    // Dos amarillas
    // =========================================================================

    @Test
    @DisplayName("RN-03 | 2 AMARILLAS en el mismo partido → suspendido con motivo 'DOS_AMARILLAS'")
    void dosAmarillasMismoPartido_suspende_conMotivoDosAmarillaS() {
        // given
        when(partidoRepository.findByTorneoIdAndNumeroFecha(TORNEO_ID, FECHA_N1))
                .thenReturn(List.of(partidoFinalizado));

        EventoPartido am1 = new EventoPartido(partidoFinalizado, jugador, TipoEvento.AMARILLA, 20);
        EventoPartido am2 = new EventoPartido(partidoFinalizado, jugador, TipoEvento.AMARILLA, 70);
        when(eventoPartidoRepository.findByPartidoId(10)).thenReturn(List.of(am1, am2));

        // when
        List<SancionResponse> sanciones = sancionService.listarSancionados(TORNEO_ID, FECHA_N);

        // then
        assertEquals(1, sanciones.size(),
                "Solo debe haber UNA sanción aunque haya dos amarillas");
        assertEquals("DOS_AMARILLAS", sanciones.get(0).getMotivo());
    }

    // =========================================================================
    // Una sola amarilla — caso borde
    // =========================================================================

    @Test
    @DisplayName("RN-03 | 1 sola AMARILLA → NO queda suspendido")
    void unaAmarilla_noSuspende() {
        // given
        when(partidoRepository.findByTorneoIdAndNumeroFecha(TORNEO_ID, FECHA_N1))
                .thenReturn(List.of(partidoFinalizado));
        when(eventoPartidoRepository.findByPartidoId(10))
                .thenReturn(List.of(new EventoPartido(partidoFinalizado, jugador, TipoEvento.AMARILLA, 20)));

        // when/then
        List<SancionResponse> sanciones = sancionService.listarSancionados(TORNEO_ID, FECHA_N);
        assertTrue(sanciones.isEmpty(), "1 amarilla no debe generar suspensión");
        assertFalse(sancionService.estaJugadorSuspendido(50, TORNEO_ID, FECHA_N));
    }

    // =========================================================================
    // obtenerJugadoresSuspendidos — integración interna
    // =========================================================================

    @Test
    @DisplayName("RN-03 | obtenerJugadoresSuspendidos() devuelve el ID del jugador con roja")
    void obtenerJugadoresSuspendidos_contieneIdJugadorConRoja() {
        when(partidoRepository.findByTorneoIdAndNumeroFecha(TORNEO_ID, FECHA_N1))
                .thenReturn(List.of(partidoFinalizado));
        when(eventoPartidoRepository.findByPartidoId(10))
                .thenReturn(List.of(new EventoPartido(partidoFinalizado, jugador, TipoEvento.ROJA, 88)));

        Set<Integer> suspendidos = sancionService.obtenerJugadoresSuspendidos(TORNEO_ID, FECHA_N);

        assertTrue(suspendidos.contains(50));
        assertEquals(1, suspendidos.size());
    }
}
