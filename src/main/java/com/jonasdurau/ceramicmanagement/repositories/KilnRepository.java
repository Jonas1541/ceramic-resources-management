package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.Kiln;

public interface KilnRepository extends JpaRepository<Kiln, Long>{
}
