package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.entity.Jugador;
import com.grupo2.LigaSF.exception.BadRequestException;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.repository.JugadorRepository;
import com.grupo2.LigaSF.service.impl.BloqueoJugadorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Módulo 4 – Reglamento: Tests unitarios de BloqueoJugadorServiceImpl.
 *
 * <p>Valida que un jugador suspendido o desactivado no pueda ser
 * inscripto ni participar en un partido.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Módulo 4 – Reglamento: Bloqueo de Jugadores")
class BloqueoJugadorServiceTest {

    @Mock
    private SancionService sancionService;

    @Mock
    private JugadorRepository jugadorRepository;

    @InjectMocks
    private BloqueoJugadorServiceImpl bloqueoJugadorService;

    // ── Datos reutilizables ───────────────────────────────────────────────────
    private static final Integer JUGADOR_ID  = 7;
    private static final Integer TORNEO_ID   = 1;
    private static final Integer NUMERO_FECHA = 3;

    private Jugador jugadorActivo;
    private Jugador jugadorDesactivado;

    @BeforeEach
    void setUp() {
        jugadorActivo = new Jugador();
        jugadorActivo.setId(JUGADOR_ID);
        jugadorActivo.setNombre("Rodrigo");
        jugadorActivo.setApellido("Gómez");
        jugadorActivo.setActivo(true);

        jugadorDesactivado = new Jugador();
        jugadorDesactivado.setId(JUGADOR_ID);
        jugadorDesactivado.setNombre("Rodrigo");
        jugadorDesactivado.setApellido("Gómez");
        jugadorDesactivado.setActivo(false);
    }

    // =========================================================================
    // estaJugadorBloqueado
    // =========================================================================

    @Test
    @DisplayName("Jugador activo sin sanción → NO está bloqueado")
    void jugadorActivoSinSancion_noEstaBloqueado() {
        // given
        when(jugadorRepository.findById(JUGADOR_ID)).thenReturn(Optional.of(jugadorActivo));
        when(sancionService.estaJugadorSuspendido(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA))
                .thenReturn(false);

        // when / then
        assertFalse(bloqueoJugadorService.estaJugadorBloqueado(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA));
    }

    @Test
    @DisplayName("Jugador DESACTIVADO → está bloqueado (aunque no tenga sanciones)")
    void jugadorDesactivado_estaBloqueado() {
        // given: el jugador está dado de baja — no necesitamos consultar sanciones
        when(jugadorRepository.findById(JUGADOR_ID)).thenReturn(Optional.of(jugadorDesactivado));

        // when / then
        assertTrue(bloqueoJugadorService.estaJugadorBloqueado(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA));
    }

    @Test
    @DisplayName("Jugador activo pero SUSPENDIDO → está bloqueado")
    void jugadorActivoSuspendido_estaBloqueado() {
        // given: el jugador está activo pero tiene suspensión por tarjetas
        when(jugadorRepository.findById(JUGADOR_ID)).thenReturn(Optional.of(jugadorActivo));
        when(sancionService.estaJugadorSuspendido(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA))
                .thenReturn(true);

        // when / then
        assertTrue(bloqueoJugadorService.estaJugadorBloqueado(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA));
    }

    // =========================================================================
    // verificarHabilitacion
    // =========================================================================

    @Test
    @DisplayName("verificarHabilitacion | Jugador habilitado → no lanza excepción")
    void verificarHabilitacion_jugadorHabilitado_noLanzaExcepcion() {
        // given
        when(jugadorRepository.findById(JUGADOR_ID)).thenReturn(Optional.of(jugadorActivo));
        when(sancionService.estaJugadorSuspendido(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA))
                .thenReturn(false);

        // when / then
        assertDoesNotThrow(() ->
                bloqueoJugadorService.verificarHabilitacion(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA)
        );
    }

    @Test
    @DisplayName("verificarHabilitacion | Jugador DESACTIVADO → BadRequestException")
    void verificarHabilitacion_jugadorDesactivado_lanzaBadRequest() {
        // given
        when(jugadorRepository.findById(JUGADOR_ID)).thenReturn(Optional.of(jugadorDesactivado));

        // when / then
        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                bloqueoJugadorService.verificarHabilitacion(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA)
        );

        assertTrue(ex.getMessage().contains("Rodrigo Gómez"),
                "El mensaje debe incluir el nombre del jugador");
        assertTrue(ex.getMessage().contains("desactivado"),
                "El mensaje debe mencionar que está desactivado");
    }

    @Test
    @DisplayName("verificarHabilitacion | Jugador SUSPENDIDO → ConflictException")
    void verificarHabilitacion_jugadorSuspendido_lanzaConflict() {
        // given: activo pero con suspensión
        when(jugadorRepository.findById(JUGADOR_ID)).thenReturn(Optional.of(jugadorActivo));
        when(sancionService.estaJugadorSuspendido(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA))
                .thenReturn(true);

        // when / then
        ConflictException ex = assertThrows(ConflictException.class, () ->
                bloqueoJugadorService.verificarHabilitacion(JUGADOR_ID, TORNEO_ID, NUMERO_FECHA)
        );

        assertTrue(ex.getMessage().contains("Rodrigo Gómez"),
                "El mensaje debe incluir el nombre del jugador");
        assertTrue(ex.getMessage().contains("RN-03"),
                "El mensaje debe referenciar la regla de negocio");
        assertTrue(ex.getMessage().contains(String.valueOf(NUMERO_FECHA)),
                "El mensaje debe indicar el número de fecha");
    }
}
