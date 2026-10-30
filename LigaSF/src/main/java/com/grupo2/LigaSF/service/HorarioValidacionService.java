package com.grupo2.LigaSF.service;

import java.time.LocalDateTime;

/**
 * Módulo 4 – Reglamento: Validación de horarios (RN-01, RN-02).
 *
 * <p>RN-01: Una cancha no puede tener dos partidos activos cuyos horarios de inicio
 * estén a menos de {@value #VENTANA_MINUTOS} minutos de diferencia.</p>
 *
 * <p>RN-02: Un árbitro no puede dirigir dos partidos cuyos horarios de inicio
 * estén a menos de {@value #VENTANA_MINUTOS} minutos de diferencia.</p>
 */
public interface HorarioValidacionService {

    /** Ventana mínima (en minutos) entre dos partidos en la misma cancha o con el mismo árbitro. */
    int VENTANA_MINUTOS = 120;

    /**
     * Verifica que la cancha indicada esté libre en el horario solicitado.
     *
     * @param canchaId       ID de la cancha a validar.
     * @param inicio         Fecha y hora de inicio del nuevo partido.
     * @param idPartidoExcluido ID del partido a excluir de la búsqueda (útil al editar).
     *                       Pasar {@code null} cuando se programa un partido nuevo.
     * @throws com.grupo2.LigaSF.exception.ConflictException si la cancha ya tiene
     *         un partido dentro de la ventana mínima.
     */
    void validarDisponibilidadCancha(Integer canchaId, LocalDateTime inicio, Integer idPartidoExcluido);

    /**
     * Verifica que el árbitro indicado esté libre en el horario solicitado.
     *
     * @param arbitroId      ID del árbitro a validar.
     * @param inicio         Fecha y hora de inicio del nuevo partido.
     * @param idPartidoExcluido ID del partido a excluir de la búsqueda (útil al editar).
     *                       Pasar {@code null} cuando se programa un partido nuevo.
     * @throws com.grupo2.LigaSF.exception.ConflictException si el árbitro ya tiene
     *         un partido dentro de la ventana mínima.
     */
    void validarDisponibilidadArbitro(Integer arbitroId, LocalDateTime inicio, Integer idPartidoExcluido);
}
