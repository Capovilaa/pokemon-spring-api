package com.pokemon.api.oak.application.usecase;

import com.pokemon.api.oak.infrastructure.ai.PokemonKnowledgeService;
import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestPokemonKnowledgeUseCase extends BaseUseCase<Resource, Void> {

    private final PokemonKnowledgeService knowledgeService;

    @Override
    public Void execute(Resource input, ExecutionContext context) {
        knowledgeService.ingest(input);
        return null;
    }
}