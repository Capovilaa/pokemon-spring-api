package com.pokemon.api.trainer.infrastructure.persistence;

import com.pokemon.api.trainer.domain.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringTrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByKeycloakId(String keycloakId);

    List<Trainer> findAllByOrderByWinsDesc();
}