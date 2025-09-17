package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.BatchEmployeeUsage;

public interface BatchEmployeeUsageRepository extends JpaRepository<BatchEmployeeUsage, Long>{

    boolean existsByEmployeeId(Long employeeId);
}
