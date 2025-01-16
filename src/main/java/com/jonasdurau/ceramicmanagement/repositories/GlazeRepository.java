package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.Glaze;

public interface GlazeRepository extends JpaRepository<Glaze, Long> {
}
