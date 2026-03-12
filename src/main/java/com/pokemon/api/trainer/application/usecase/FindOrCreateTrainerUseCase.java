package com.pokemon.api.trainer.application.usecase;

import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindOrCreateTrainerUseCase extends BaseUseCase<Void, Trainer> {

    private final TrainerRepository trainerRepository;

    @Override
    public Trainer execute(Void input, ExecutionContext context) {
        return trainerRepository.findByKeycloakId(context.user().id())
                .orElseGet(() -> trainerRepository.save(
                        Trainer.builder()
                                .keycloakId(context.user().id())
                                .username(context.user().username())
                                .email(context.user().email())
                                .build()
                ));
    }
}