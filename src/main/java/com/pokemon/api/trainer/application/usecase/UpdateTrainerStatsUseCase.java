package com.pokemon.api.trainer.application.usecase;

import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateTrainerStatsUseCase extends BaseUseCase<UpdateTrainerStatsUseCase.Input, Void> {

    private final TrainerRepository trainerRepository;

    public record Input(Trainer winner, Trainer loser) {
    }

    @Override
    public Void execute(Input input, ExecutionContext context) {
        Trainer winner = input.winner();
        winner.setWins(winner.getWins() + 1);
        winner.setTotalBattles(winner.getTotalBattles() + 1);
        trainerRepository.save(winner);

        Trainer loser = input.loser();
        loser.setLosses(loser.getLosses() + 1);
        loser.setTotalBattles(loser.getTotalBattles() + 1);
        trainerRepository.save(loser);

        log.info("Stats updated — winner: {} (wins: {}), loser: {} (losses: {})",
                winner.getUsername(), winner.getWins(),
                loser.getUsername(), loser.getLosses());

        return null;
    }
}