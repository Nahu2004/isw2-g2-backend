package com.grupo2.LigaSF.repository;

import com.grupo2.LigaSF.entity.Partido;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Integer> {

    List<Partido> findByTorneoIdAndNumeroFechaOrderByFechaHoraInicioAsc(Integer torneoId, Integer numeroFecha);

    // Usado por SancionServiceImpl (Módulo 4) para obtener partidos de una fecha sin orden específico
    List<Partido> findByTorneoIdAndNumeroFecha(Integer torneoId, Integer numeroFecha);

    List<Partido> findByTorneoIdOrderByNumeroFechaAscFechaHoraInicioAsc(Integer torneoId);

    List<Partido> findByCanchaIdAndEstadoNot(Integer canchaId, EstadoPartido estado);

    List<Partido> findByArbitroIdAndEstadoNot(Integer arbitroId, EstadoPartido estado);

    @Query("SELECT p FROM Partido p WHERE p.torneo.id = :torneoId AND p.numeroFecha = :fecha AND " +
           "(p.equipoLocal.id = :equipoId OR p.equipoVisitante.id = :equipoId)")
    List<Partido> findPartidosEquipoEnFecha(@Param("torneoId") Integer torneoId,
                                           @Param("fecha") Integer fecha,
                                           @Param("equipoId") Integer equipoId);
}
