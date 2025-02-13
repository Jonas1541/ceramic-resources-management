package com.jonasdurau.ceramicmanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.BisqueFiring;

public interface BisqueFiringRepository extends JpaRepository<BisqueFiring, Long>{

    boolean existsByKilnId(Long kilnId);

    List<BisqueFiring> findByKilnId(Long KilnId);

    Optional<BisqueFiring> findByIdAndKilnId(Long id, Long kilnId);
}
