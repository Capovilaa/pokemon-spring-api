package com.pokemon.api.oak.infrastructure.web;

import com.pokemon.api.oak.application.usecase.*;
import com.pokemon.api.oak.infrastructure.web.dto.CreateConversationResponse;
import com.pokemon.api.oak.infrastructure.web.dto.OakQuestionRequest;
import com.pokemon.api.oak.infrastructure.web.dto.OakResponse;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Tag(name = "Oak", description = "Talk with professor Oak")
@RestController
@RequestMapping("/api/v1/oak")
@RequiredArgsConstructor
public class OakController {

    private final AskOakUseCase askOakUseCase;
    private final AnalyzeBattleUseCase analyzeBattleUseCase;
    private final PokedexAdviceUseCase pokedexAdviceUseCase;
    private final CreateConversationUseCase createConversationUseCase;
    private final StreamOakUseCase streamOakUseCase;
    private final IngestPokemonKnowledgeUseCase ingestPokemonKnowledgeUseCase;
    private final AskOakWithRagUseCase askOakWithRagUseCase;

    @PostMapping("/conversations")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<CreateConversationResponse> createConversation() {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        String conversationId = createConversationUseCase.execute(null, context);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateConversationResponse(conversationId));
    }

    @PostMapping("/ask")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<OakResponse> ask(@Valid @RequestBody OakQuestionRequest request) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        var input = new AskOakUseCase.Input(request.question(), request.conversationId());
        return ResponseEntity.ok(askOakUseCase.execute(input, context));
    }

    @GetMapping("/analyze-battle/{battleId}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<OakResponse> analyzeBattle(@PathVariable Long battleId) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(analyzeBattleUseCase.execute(battleId, context));
    }

    @GetMapping("/pokedex-advice")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<OakResponse> pokedexAdvice() {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(pokedexAdviceUseCase.execute(null, context));
    }

    @PostMapping(value = "/ask/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public Flux<String> stream(@Valid @RequestBody OakQuestionRequest request) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        var input = new AskOakUseCase.Input(request.question(), request.conversationId());
        return streamOakUseCase.execute(input, context);
    }

    @PostMapping("/knowledge")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<Void> ingestKnowledge(@RequestParam("file") MultipartFile file) throws IOException {
        var resource = file.getResource();
        ingestPokemonKnowledgeUseCase.execute(resource, ExecutionContext.empty());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ask/rag")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<OakResponse> askWithRag(@RequestBody AskOakUseCase.Input input) {
        var context = ExecutionContext.of(SecurityUtils.getAuthenticatedUser());
        return ResponseEntity.ok(askOakWithRagUseCase.execute(input.question(), context));
    }
}