package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.exception.ConflictException;
import com.grupo2.LigaSF.repository.ArbitroRepository;
import com.grupo2.LigaSF.repository.CanchaRepository;
import com.grupo2.LigaSF.repository.PartidoRepository;
import com.grupo2.LigaSF.service.HorarioValidacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Módulo 4 – Reglamento: Implementación de la validación de horarios.
 *
 * <p>Detecta solapamientos de cancha (RN-01) y árbitro (RN-02) comparando
 * la diferencia en minutos entre el inicio solicitado y los partidos ya
 * registrados que no estén en estado {@link EstadoPartido#SUSPENDIDO}.</p>
 */
@Service
@Transactional(readOnly = true)
public class HorarioValidacionServiceImpl implements HorarioValidacionService {

    private final PartidoRepository partidoRepository;
    private final CanchaRepository canchaRepository;
    private final ArbitroRepository arbitroRepository;

    public HorarioValidacionServiceImpl(PartidoRepository partidoRepository,
                                        CanchaRepository canchaRepository,
                                        ArbitroRepository arbitroRepository) {
        this.partidoRepository = partidoRepository;
        this.canchaRepository = canchaRepository;
        this.arbitroRepository = arbitroRepository;
    }

    // ---------------------------------------------------------------
    // RN-01: Validación de disponibilidad de cancha
    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Recupera todos los partidos activos (no suspendidos) de la cancha
     * y verifica que ninguno tenga un inicio dentro de la ventana mínima
     * respecto al horario solicitado.</p>
     */
    @Override
    public void validarDisponibilidadCancha(Integer canchaId, LocalDateTime inicio, Integer idPartidoExcluido) {

        // Nombre de la cancha para el mensaje de error (legible para el usuario)
        String nombreCancha = canchaRepository.findById(canchaId)
                .map(c -> c.getNombre())
                .orElse("ID " + canchaId);

        List<Partido> partidosCancha =
                partidoRepository.findByCanchaIdAndEstadoNot(canchaId, EstadoPartido.SUSPENDIDO);

        for (Partido p : partidosCancha) {
            // Al editar un partido existente, lo excluimos para que no choque consigo mismo
            if (idPartidoExcluido != null && p.getId().equals(idPartidoExcluido)) {
                continue;
            }

            long diffMinutos = Math.abs(
                    Duration.between(inicio, p.getFechaHoraInicio()).toMinutes()
            );

            if (diffMinutos < VENTANA_MINUTOS) {
                throw new ConflictException(
                        "La cancha '" + nombreCancha + "' ya tiene un partido programado a las "
                        + p.getFechaHoraInicio()
                        + ". Deben existir al menos " + VENTANA_MINUTOS
                        + " minutos de diferencia entre inicios (RN-01)."
                );
            }
        }
    }

    // ---------------------------------------------------------------
    // RN-02: Validación de disponibilidad de árbitro
    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Recupera todos los partidos activos (no suspendidos) del árbitro
     * y verifica que ninguno tenga un inicio dentro de la ventana mínima
     * respecto al horario solicitado.</p>
     */
    @Override
    public void validarDisponibilidadArbitro(Integer arbitroId, LocalDateTime inicio, Integer idPartidoExcluido) {

        // Nombre del árbitro para el mensaje de error (legible para el usuario)
        String nombreArbitro = arbitroRepository.findById(arbitroId)
                .map(a -> a.getNombre() + " " + a.getApellido())
                .orElse("ID " + arbitroId);

        List<Partido> partidosArbitro =
                partidoRepository.findByArbitroIdAndEstadoNot(arbitroId, EstadoPartido.SUSPENDIDO);

        for (Partido p : partidosArbitro) {
            // Al editar un partido existente, lo excluimos para que no choque consigo mismo
            if (idPartidoExcluido != null && p.getId().equals(idPartidoExcluido)) {
                continue;
            }

            long diffMinutos = Math.abs(
                    Duration.between(inicio, p.getFechaHoraInicio()).toMinutes()
            );

            if (diffMinutos < VENTANA_MINUTOS) {
                throw new ConflictException(
                        "El árbitro '" + nombreArbitro + "' ya tiene un partido asignado a las "
                        + p.getFechaHoraInicio()
                        + ". Deben existir al menos " + VENTANA_MINUTOS
                        + " minutos de diferencia entre inicios (RN-02)."
                );
            }
        }
    }
}
