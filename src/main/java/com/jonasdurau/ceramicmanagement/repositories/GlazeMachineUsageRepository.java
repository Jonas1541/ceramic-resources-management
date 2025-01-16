package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.GlazeMachineUsage;

public interface GlazeMachineUsageRepository extends JpaRepository<GlazeMachineUsage, Long> {

    boolean existsByMachineId(Long machineId);
}
