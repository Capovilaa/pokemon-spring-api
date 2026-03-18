package com.pokemon.api.oak.infrastructure.ai.tools;

import com.pokemon.api.trainer.domain.repository.PokedexRepository;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import com.pokemon.api.trainer.domain.entity.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@Description("Get the Pokédex of the current trainer, showing all species they have already encountered and caught")
@RequiredArgsConstructor
public class GetTrainerPokedexTool implements Function<GetTrainerPokedexTool.Request, GetTrainerPokedexTool.Response> {

    private final PokedexRepository pokedexRepository;
    private final TrainerRepository trainerRepository;

    public record Request(String trainerKeycloakId) {
    }

    public record PokedexEntrySummary(
            Integer speciesId,
            String speciesName
    ) {
    }

    public record Response(
            List<PokedexEntrySummary> entries,
            long totalCaught,
            long totalKanto
    ) {
    }

    @Override
    public Response apply(Request request) {
        Trainer trainer = trainerRepository
                .findByKeycloakId(request.trainerKeycloakId())
                .orElseThrow();

        var entries = pokedexRepository.findByTrainer(trainer);
        long total = pokedexRepository.countByTrainer(trainer);

        List<PokedexEntrySummary> summaries = entries.stream()
                .map(e -> new PokedexEntrySummary(
                        e.getSpeciesId(),
                        e.getSpeciesName()
                ))
                .toList();

        return new Response(summaries, total, 151);
    }
}