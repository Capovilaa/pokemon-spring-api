package com.pokemon.api.trainer.domain.repository;

import com.pokemon.api.trainer.domain.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Optional<Trainer> findByKeycloakId(String keycloakId);

    Optional<Trainer> findById(Long id);

    List<Trainer> findAllOrderByWinsDesc();

    Trainer save(Trainer trainer);
}