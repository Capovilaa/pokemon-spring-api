package com.pokemon.api.oak.infrastructure.ai.tools;

import com.pokemon.api.battle.domain.repository.BattleRepository;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Component
@Description("Get the battle history of the current trainer, showing wins, losses, Pokémon used and opponent details")
@RequiredArgsConstructor
public class GetBattleHistoryTool implements Function<GetBattleHistoryTool.Request, GetBattleHistoryTool.Response> {

    private final BattleRepository battleRepository;
    private final TrainerRepository trainerRepository;

    public record Request(String trainerKeycloakId) {
    }

    public record BattleSummary(
            Long battleId,
            String opponentTrainer,
            String myPokemon,
            String opponentPokemon,
            boolean iWon,
            Integer totalTurns,
            LocalDateTime foughtAt
    ) {
    }

    public record Response(List<BattleSummary> battles, int totalWins, int totalLosses) {
    }

    @Override
    public Response apply(Request request) {
        Trainer trainer = trainerRepository
                .findByKeycloakId(request.trainerKeycloakId())
                .orElseThrow();

        var battles = battleRepository.findByTrainer(trainer);

        List<BattleSummary> summaries = battles.stream()
                .map(b -> {
                    boolean iWon = b.getWinnerTrainer().getId().equals(trainer.getId());
                    String opponent = iWon
                            ? b.getDefenderTrainer().getUsername()
                            : b.getAttackerTrainer().getUsername();
                    String myPokemon = iWon
                            ? b.getWinnerPokemon().getName()
                            : b.getDefenderPokemon().getName();
                    String opponentPokemon = iWon
                            ? b.getDefenderPokemon().getName()
                            : b.getWinnerPokemon().getName();

                    return new BattleSummary(
                            b.getId(),
                            opponent,
                            myPokemon,
                            opponentPokemon,
                            iWon,
                            b.getTotalTurns(),
                            b.getFoughtAt()
                    );
                })
                .toList();

        int wins = (int) summaries.stream().filter(BattleSummary::iWon).count();
        int losses = summaries.size() - wins;

        return new Response(summaries, wins, losses);
    }
}