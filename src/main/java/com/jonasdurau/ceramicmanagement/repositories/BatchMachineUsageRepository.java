package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.BatchMachineUsage;

public interface BatchMachineUsageRepository extends JpaRepository<BatchMachineUsage, Long> {

    boolean existsByMachineId(Long machineId);
}
