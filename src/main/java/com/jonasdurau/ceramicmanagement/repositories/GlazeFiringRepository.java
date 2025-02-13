package com.jonasdurau.ceramicmanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.GlazeFiring;

public interface GlazeFiringRepository extends JpaRepository<GlazeFiring, Long>{

    boolean existsByKilnId(Long kilnId);

    List<GlazeFiring> findByKilnId(Long KilnId);

    Optional<GlazeFiring> findByIdAndKilnId(Long id, Long kilnId);
}
