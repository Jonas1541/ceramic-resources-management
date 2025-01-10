package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.Batch;

public interface BatchRepository extends JpaRepository<Batch, Long> {
}
