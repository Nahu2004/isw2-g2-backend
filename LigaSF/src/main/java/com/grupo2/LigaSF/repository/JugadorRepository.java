package com.grupo2.LigaSF.repository;

import com.grupo2.LigaSF.entity.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Integer> {
    List<Jugador> findByEquipoIdAndActivoTrue(Integer equipoId);
    List<Jugador> findByActivoTrue();
    Optional<Jugador> findByIdAndActivoTrue(Integer id);
    boolean existsByDni(String dni);
    boolean existsByDniAndIdNot(String dni, Integer id);
}
