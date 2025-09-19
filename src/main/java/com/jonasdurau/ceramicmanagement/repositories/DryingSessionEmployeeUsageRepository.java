package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.DryingSessionEmployeeUsage;

public interface DryingSessionEmployeeUsageRepository extends JpaRepository<DryingSessionEmployeeUsage, Long> {
    boolean existsByEmployeeId(Long employeeId);
}
