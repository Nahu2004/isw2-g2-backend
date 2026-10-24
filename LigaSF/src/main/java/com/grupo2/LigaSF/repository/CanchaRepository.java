package com.grupo2.LigaSF.repository;

import com.grupo2.LigaSF.entity.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Integer> {
    List<Cancha> findByActivoTrue();
    Optional<Cancha> findByIdAndActivoTrue(Integer id);
    boolean existsByNombreIgnoreCase(String nombre);
}
