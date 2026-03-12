package com.pokemon.api.trainer.domain.repository;

import com.pokemon.api.trainer.domain.entity.Trainer;

import java.util.Optional;

public interface TrainerRepository {

    Trainer save(Trainer trainer);

    Optional<Trainer> findByKeycloakId(String keycloakId);

    boolean existsByKeycloakId(String keycloakId);
}