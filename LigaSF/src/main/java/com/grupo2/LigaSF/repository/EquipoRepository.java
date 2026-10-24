package com.grupo2.LigaSF.repository;

import com.grupo2.LigaSF.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Integer> {
    List<Equipo> findByTorneoIdAndActivoTrue(Integer torneoId);
    List<Equipo> findByActivoTrue();
    Optional<Equipo> findByIdAndActivoTrue(Integer id);
    boolean existsByTorneoIdAndNombreIgnoreCase(Integer torneoId, String nombre);
    boolean existsByTorneoIdAndNombreIgnoreCaseAndIdNot(Integer torneoId, String nombre, Integer id);
}
