package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.DryingRoom;

public interface DryingRoomRepository extends JpaRepository<DryingRoom, Long>{

    boolean existsByMachinesId(Long machinesId);

    boolean existsByName(String name);
}
