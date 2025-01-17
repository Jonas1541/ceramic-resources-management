package com.jonasdurau.ceramicmanagement.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.GlazeMachineUsage;

public interface GlazeMachineUsageRepository extends JpaRepository<GlazeMachineUsage, Long> {

    boolean existsByMachineId(Long machineId);

    List<GlazeMachineUsage> findByMachineId(Long machineId);
}
