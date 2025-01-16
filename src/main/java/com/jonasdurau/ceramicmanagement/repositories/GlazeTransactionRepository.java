package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.GlazeTransaction;

public interface GlazeTransactionRepository extends JpaRepository<GlazeTransaction, Long> {

    boolean existsByGlazeId(Long glazeId);
}
