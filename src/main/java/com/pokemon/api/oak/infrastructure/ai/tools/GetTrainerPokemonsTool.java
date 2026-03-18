package com.pokemon.api.oak.infrastructure.ai.tools;

import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
@Description("Get all Pokémon owned by the current trainer, including their stats, types and level")
@RequiredArgsConstructor
public class GetTrainerPokemonsTool implements Function<GetTrainerPokemonsTool.Request, GetTrainerPokemonsTool.Response> {

    private final PokemonRepository pokemonRepository;
    private final TrainerRepository trainerRepository;

    public record Request(String trainerKeycloakId) {
    }

    public record PokemonSummary(
            Long id,
            String name,
            Integer level,
            Integer hp,
            Integer attack,
            Integer defense,
            Integer speed,
            List<String> types
    ) {
    }

    public record Response(List<PokemonSummary> pokemons) {
    }

    @Override
    public Response apply(Request request) {
        Trainer trainer = trainerRepository
                .findByKeycloakId(request.trainerKeycloakId())
                .orElseThrow();

        List<PokemonSummary> summaries = pokemonRepository
                .findByTrainer(trainer)
                .stream()
                .map(p -> new PokemonSummary(
                        p.getId(),
                        p.getName(),
                        p.getLevel(),
                        p.getBaseHp(),
                        p.getBaseAttack(),
                        p.getBaseDefense(),
                        p.getBaseSpeed(),
                        p.getTypes().stream().map(t -> t.getName()).toList()
                ))
                .toList();

        return new Response(summaries);
    }
}