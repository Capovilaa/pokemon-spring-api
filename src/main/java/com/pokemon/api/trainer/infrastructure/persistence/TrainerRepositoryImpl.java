package com.pokemon.api.trainer.infrastructure.persistence;

import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainerRepositoryImpl implements TrainerRepository {

    private final SpringTrainerRepository springTrainerRepository;

    @Override
    public Optional<Trainer> findByKeycloakId(String keycloakId) {
        return springTrainerRepository.findByKeycloakId(keycloakId);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return springTrainerRepository.findById(id);
    }

    @Override
    public List<Trainer> findAllOrderByWinsDesc() {
        return springTrainerRepository.findAllByOrderByWinsDesc();
    }

    @Override
    public Trainer save(Trainer trainer) {
        return springTrainerRepository.save(trainer);
    }
}