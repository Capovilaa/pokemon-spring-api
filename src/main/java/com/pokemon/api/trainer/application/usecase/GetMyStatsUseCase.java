package com.pokemon.api.trainer.application.usecase;

import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import com.pokemon.api.trainer.infrastructure.web.dto.TrainerMapper;
import com.pokemon.api.trainer.infrastructure.web.dto.TrainerStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMyStatsUseCase extends BaseUseCase<Void, TrainerStatsResponse> {

    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;

    @Override
    public TrainerStatsResponse execute(Void input, ExecutionContext context) {
        var trainer = trainerRepository
                .findByKeycloakId(context.user().id())
                .orElseThrow(() -> new NotFoundException("Trainer", context.user().id()));

        return trainerMapper.toStatsResponse(trainer);
    }
}