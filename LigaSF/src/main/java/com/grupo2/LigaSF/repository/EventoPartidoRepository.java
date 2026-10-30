package com.grupo2.LigaSF.repository;

import com.grupo2.LigaSF.entity.EventoPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoPartidoRepository extends JpaRepository<EventoPartido, Integer> {
    List<EventoPartido> findByPartidoId(Integer partidoId);
    List<EventoPartido> findByJugadorId(Integer jugadorId);
}
