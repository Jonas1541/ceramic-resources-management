package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.Machine;

public interface MachineRepository extends JpaRepository<Machine, Long>{

    boolean existsByName(String name);
}
