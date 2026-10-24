package com.grupo2.LigaSF.repository;

import com.grupo2.LigaSF.entity.Arbitro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArbitroRepository extends JpaRepository<Arbitro, Integer> {
    List<Arbitro> findByActivoTrue();
    Optional<Arbitro> findByIdAndActivoTrue(Integer id);
    boolean existsByDni(String dni);
}
