package com.pokemon.api.oak.infrastructure.ai.tools;

import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@Description("Get the global trainer ranking ordered by wins, showing username, wins, losses and win rate")
@RequiredArgsConstructor
public class GetRankingTool implements Function<GetRankingTool.Request, GetRankingTool.Response> {

    private final TrainerRepository trainerRepository;

    public record Request(String ignored) {
    }

    public record TrainerRankingSummary(
            String username,
            Integer wins,
            Integer losses,
            Integer totalBattles,
            Double winRate
    ) {
    }

    public record Response(List<TrainerRankingSummary> ranking) {
    }

    @Override
    public Response apply(Request request) {
        List<TrainerRankingSummary> ranking = trainerRepository.findAllOrderByWinsDesc()
                .stream()
                .map(t -> new TrainerRankingSummary(
                        t.getUsername(),
                        t.getWins(),
                        t.getLosses(),
                        t.getTotalBattles(),
                        t.getTotalBattles() > 0
                                ? (double) t.getWins() / t.getTotalBattles() * 100
                                : 0.0
                ))
                .toList();

        return new Response(ranking);
    }
}