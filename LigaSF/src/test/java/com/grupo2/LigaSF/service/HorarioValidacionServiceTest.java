package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.entity.Arbitro;
import com.grupo2.LigaSF.entity.Cancha;
import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.repository.ArbitroRepository;
import com.grupo2.LigaSF.repository.CanchaRepository;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.impl.HorarioValidacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Módulo 4 – Reglamento: Tests unitarios de HorarioValidacionServiceImpl.
 *
 * <p>Valida RN-01 (cancha) y RN-02 (árbitro) sin levantar el contexto de Spring.
 * Todos los repositorios son mocks de Mockito.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Módulo 4 – Reglamento: Validación de Horarios")
class HorarioValidacionServiceTest {

    // ── Mocks de los repositorios ─────────────────────────────────────────────
    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private CanchaRepository canchaRepository;

    @Mock
    private ArbitroRepository arbitroRepository;

    // ── Clase bajo prueba ─────────────────────────────────────────────────────
    @InjectMocks
    private HorarioValidacionServiceImpl horarioValidacionService;

    // ── Datos de prueba reutilizables ─────────────────────────────────────────
    private static final Integer CANCHA_ID  = 1;
    private static final Integer ARBITRO_ID = 10;
    private static final LocalDateTime INICIO_SOLICITADO =
            LocalDateTime.of(2025, 6, 14, 10, 0); // sábado 10:00 hs

    private Cancha canchaFake;
    private Arbitro arbitroFake;

    @BeforeEach
    void setUp() {
        canchaFake = new Cancha("Cancha Norte", "Av. Siempre Viva 123", true);
        canchaFake.setId(CANCHA_ID);

        arbitroFake = new Arbitro("Carlos", "Pérez", "12345678", true);
        arbitroFake.setId(ARBITRO_ID);
    }

    // =========================================================================
    // RN-01: Validación de cancha
    // =========================================================================

    @Test
    @DisplayName("RN-01 | Cancha sin partidos previos → no lanza excepción")
    void cancha_sinPartidos_noLanzaExcepcion() {
        // given: la cancha no tiene ningún partido activo
        when(canchaRepository.findById(CANCHA_ID)).thenReturn(Optional.of(canchaFake));
        when(partidoRepository.findByCanchaIdAndEstadoNot(CANCHA_ID, EstadoPartido.SUSPENDIDO))
                .thenReturn(List.of());

        // when/then: no debe lanzar ninguna excepción
        assertDoesNotThrow(() ->
                horarioValidacionService.validarDisponibilidadCancha(CANCHA_ID, INICIO_SOLICITADO, null)
        );
    }

    @Test
    @DisplayName("RN-01 | Cancha con partido a 60 min (dentro de ventana) → ConflictException")
    void cancha_conflictoA60Minutos_lanzaConflictException() {
        // given: hay un partido existente 60 minutos después del solicitado
        Partido partidoExistente = crearPartidoFake(99, INICIO_SOLICITADO.plusMinutes(60), EstadoPartido.PROGRAMADO);

        when(canchaRepository.findById(CANCHA_ID)).thenReturn(Optional.of(canchaFake));
        when(partidoRepository.findByCanchaIdAndEstadoNot(CANCHA_ID, EstadoPartido.SUSPENDIDO))
                .thenReturn(List.of(partidoExistente));

        // when/then: debe lanzar ConflictException con mensaje informativo
        ConflictException ex = assertThrows(ConflictException.class, () ->
                horarioValidacionService.validarDisponibilidadCancha(CANCHA_ID, INICIO_SOLICITADO, null)
        );

        assertTrue(ex.getMessage().contains("Cancha Norte"),
                "El mensaje debe incluir el nombre de la cancha");
        assertTrue(ex.getMessage().contains("RN-01"),
                "El mensaje debe referenciar la regla de negocio");
    }

    @Test
    @DisplayName("RN-01 | Cancha con partido a exactamente 120 min (caso borde) → no lanza excepción")
    void cancha_conflictoExacto120Minutos_noLanzaExcepcion() {
        // given: hay un partido a exactamente 120 minutos — el límite exacto NO es conflicto
        Partido partidoExistente = crearPartidoFake(99, INICIO_SOLICITADO.plusMinutes(120), EstadoPartido.PROGRAMADO);

        when(canchaRepository.findById(CANCHA_ID)).thenReturn(Optional.of(canchaFake));
        when(partidoRepository.findByCanchaIdAndEstadoNot(CANCHA_ID, EstadoPartido.SUSPENDIDO))
                .thenReturn(List.of(partidoExistente));

        // when/then: 120 minutos exactos no es conflicto (la regla es "menor que 120")
        assertDoesNotThrow(() ->
                horarioValidacionService.validarDisponibilidadCancha(CANCHA_ID, INICIO_SOLICITADO, null)
        );
    }

    @Test
    @DisplayName("RN-01 | Editar partido propio (idExcluido = propio) → no lanza excepción")
    void cancha_editarPartidoPropio_noLanzaExcepcion() {
        // given: el único partido existente ES el que estamos editando (mismo id)
        final Integer ID_PARTIDO_EDITADO = 5;
        Partido mismoPartido = crearPartidoFake(ID_PARTIDO_EDITADO, INICIO_SOLICITADO, EstadoPartido.PROGRAMADO);

        when(canchaRepository.findById(CANCHA_ID)).thenReturn(Optional.of(canchaFake));
        when(partidoRepository.findByCanchaIdAndEstadoNot(CANCHA_ID, EstadoPartido.SUSPENDIDO))
                .thenReturn(List.of(mismoPartido));

        // when/then: al pasarle su propio ID como excluido, no debe chocar consigo mismo
        assertDoesNotThrow(() ->
                horarioValidacionService.validarDisponibilidadCancha(CANCHA_ID, INICIO_SOLICITADO, ID_PARTIDO_EDITADO)
        );
    }

    // =========================================================================
    // RN-02: Validación de árbitro
    // =========================================================================

    @Test
    @DisplayName("RN-02 | Árbitro con partido a 30 min (dentro de ventana) → ConflictException")
    void arbitro_conflictoA30Minutos_lanzaConflictException() {
        // given: el árbitro ya tiene un partido 30 minutos después del solicitado
        Partido partidoExistente = crearPartidoFake(88, INICIO_SOLICITADO.plusMinutes(30), EstadoPartido.PROGRAMADO);

        when(arbitroRepository.findById(ARBITRO_ID)).thenReturn(Optional.of(arbitroFake));
        when(partidoRepository.findByArbitroIdAndEstadoNot(ARBITRO_ID, EstadoPartido.SUSPENDIDO))
                .thenReturn(List.of(partidoExistente));

        // when/then: debe lanzar ConflictException con nombre del árbitro
        ConflictException ex = assertThrows(ConflictException.class, () ->
                horarioValidacionService.validarDisponibilidadArbitro(ARBITRO_ID, INICIO_SOLICITADO, null)
        );

        assertTrue(ex.getMessage().contains("Carlos Pérez"),
                "El mensaje debe incluir el nombre completo del árbitro");
        assertTrue(ex.getMessage().contains("RN-02"),
                "El mensaje debe referenciar la regla de negocio");
    }

    @Test
    @DisplayName("RN-02 | Árbitro sin partidos previos → no lanza excepción")
    void arbitro_sinPartidos_noLanzaExcepcion() {
        // given: el árbitro no tiene ningún partido activo
        when(arbitroRepository.findById(ARBITRO_ID)).thenReturn(Optional.of(arbitroFake));
        when(partidoRepository.findByArbitroIdAndEstadoNot(ARBITRO_ID, EstadoPartido.SUSPENDIDO))
                .thenReturn(List.of());

        // when/then: no debe lanzar ninguna excepción
        assertDoesNotThrow(() ->
                horarioValidacionService.validarDisponibilidadArbitro(ARBITRO_ID, INICIO_SOLICITADO, null)
        );
    }

    // =========================================================================
    // Método helper privado
    // =========================================================================

    /**
     * Crea un Partido "falso" con los campos mínimos necesarios para los tests.
     * No necesita estar en la BD — Mockito lo devuelve directamente.
     */
    private Partido crearPartidoFake(Integer id, LocalDateTime inicio, EstadoPartido estado) {
        Partido p = new Partido();
        p.setId(id);
        p.setFechaHoraInicio(inicio);
        p.setEstado(estado);
        return p;
    }
}
