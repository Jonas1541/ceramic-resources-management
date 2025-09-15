package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.Employee;
import com.jonasdurau.ceramicmanagement.entities.EmployeeCategory;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{

    boolean existsByCategory(EmployeeCategory category);
}
