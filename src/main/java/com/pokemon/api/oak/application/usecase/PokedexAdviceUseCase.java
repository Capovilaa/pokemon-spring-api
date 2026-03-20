package com.pokemon.api.oak.application.usecase;

import com.pokemon.api.oak.application.OakPersonality;
import com.pokemon.api.oak.infrastructure.web.dto.OakResponse;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.trainer.domain.repository.PokedexRepository;
import com.pokemon.api.trainer.domain.repository.TrainerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PokedexAdviceUseCase extends BaseUseCase<Void, OakResponse> {

    private final ChatModel chatModel;
    private final TrainerRepository trainerRepository;
    private final PokedexRepository pokedexRepository;

    public PokedexAdviceUseCase(
            @Qualifier("anthropicChatModel") ChatModel chatModel,
            TrainerRepository trainerRepository,
            PokedexRepository pokedexRepository) {
        this.chatModel = chatModel;
        this.trainerRepository = trainerRepository;
        this.pokedexRepository = pokedexRepository;
    }

    @Override
    public OakResponse execute(Void input, ExecutionContext context) {
        Trainer trainer = trainerRepository
                .findByKeycloakId(context.user().id())
                .orElseThrow(() -> new NotFoundException("Trainer", context.user().id()));

        var entries = pokedexRepository.findByTrainer(trainer);
        long total = pokedexRepository.countByTrainer(trainer);

        String caught = entries.stream()
                .map(e -> e.getSpeciesName() + " (#" + e.getSpeciesId() + ")")
                .collect(Collectors.joining(", "));

        String question = """
                Trainer "%s" has caught %d out of 151 Kanto Pokémon.
                
                Pokémon already caught: %s
                
                As Professor Oak, give personalized advice:
                1. Which Pokémon families are missing that would complete their collection?
                2. Which 3 Pokémon should they prioritize capturing next and why?
                3. A motivational message about their progress.
                """.formatted(trainer.getUsername(), total, caught);

        log.info("Professor Oak giving Pokédex advice to '{}'", trainer.getUsername());

        var prompt = new Prompt(List.of(
                new SystemMessage(OakPersonality.SYSTEM_PROMPT),
                new UserMessage(question)
        ));

        var response = chatModel.call(prompt);
        var metadata = response.getMetadata();

        return OakResponse.builder()
                .answer(response.getResult().getOutput().getText())
                .model(metadata.getModel())
                .inputTokens(metadata.getUsage() != null
                        ? metadata.getUsage().getPromptTokens() : 0)
                .outputTokens(metadata.getUsage() != null
                        ? metadata.getUsage().getCompletionTokens() : 0)
                .build();
    }
}