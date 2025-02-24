package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.DryingSession;

public interface DryingSessionRepository extends JpaRepository<DryingSession, Long>{

    boolean existsByDryingRoomId(Long dryingRoomId);
}
