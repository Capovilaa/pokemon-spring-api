package com.pokemon.api.trainer.application.usecase;

import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import com.pokemon.api.trainer.infrastructure.web.dto.TrainerMapper;
import com.pokemon.api.trainer.infrastructure.web.dto.TrainerStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRankingUseCase extends BaseUseCase<Void, List<TrainerStatsResponse>> {

    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;

    @Override
    public List<TrainerStatsResponse> execute(Void input, ExecutionContext context) {
        return trainerRepository.findAllOrderByWinsDesc()
                .stream()
                .map(trainerMapper::toStatsResponse)
                .toList();
    }
}