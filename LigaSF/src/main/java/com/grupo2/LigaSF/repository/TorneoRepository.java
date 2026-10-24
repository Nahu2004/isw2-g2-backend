package com.grupo2.LigaSF.repository;

import com.grupo2.LigaSF.entity.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {
    List<Torneo> findByActivoTrue();
}
