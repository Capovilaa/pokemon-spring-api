package com.pokemon.api.trainer.application.usecase;

import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.trainer.domain.entity.PokedexEntry;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.PokedexRepository;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import com.pokemon.api.trainer.infrastructure.web.dto.PokedexEntryResponse;
import com.pokemon.api.trainer.infrastructure.web.dto.PokedexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPokedexUseCase extends BaseUseCase<Void, PokedexResponse> {

    // Total de espécies na Gen 1 (Kanto)
    private static final int TOTAL_KANTO = 151;

    private final PokedexRepository pokedexRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public PokedexResponse execute(Void input, ExecutionContext context) {
        Trainer trainer = trainerRepository
                .findByKeycloakId(context.user().id())
                .orElseThrow(() -> new NotFoundException("Trainer", context.user().id()));

        List<PokedexEntry> entries = pokedexRepository.findByTrainer(trainer);

        List<PokedexEntryResponse> entryResponses = entries.stream()
                .map(e -> PokedexEntryResponse.builder()
                        .speciesId(e.getSpeciesId())
                        .speciesName(e.getSpeciesName())
                        .spriteUrl(e.getSpriteUrl())
                        .caughtAt(e.getCaughtAt())
                        .build())
                .sorted((a, b) -> a.speciesId().compareTo(b.speciesId()))
                .toList();

        int totalCaught = entries.size();
        double percentage = Math.round((totalCaught * 100.0 / TOTAL_KANTO) * 10.0) / 10.0;

        return PokedexResponse.builder()
                .totalCaught(totalCaught)
                .totalPossible(TOTAL_KANTO)
                .completionPercentage(percentage)
                .entries(entryResponses)
                .build();
    }
}