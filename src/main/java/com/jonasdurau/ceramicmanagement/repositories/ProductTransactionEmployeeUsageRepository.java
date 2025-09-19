package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.ProductTransactionEmployeeUsage;

public interface ProductTransactionEmployeeUsageRepository extends JpaRepository<ProductTransactionEmployeeUsage, Long> {
    
    boolean existsByEmployeeId(Long employeeId);
}
