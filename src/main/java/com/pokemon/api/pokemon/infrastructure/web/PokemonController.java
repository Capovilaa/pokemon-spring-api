package com.pokemon.api.pokemon.infrastructure.web;

import com.pokemon.api.pokemon.application.usecase.*;
import com.pokemon.api.pokemon.infrastructure.web.dto.CreatePokemonRequest;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.pokemon.infrastructure.web.dto.UpdatePokemonRequest;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pokemons")
@RequiredArgsConstructor
public class PokemonController {

    private final CreatePokemonUseCase createPokemonUseCase;
    private final FindPokemonUseCase findPokemonUseCase;
    private final FindAllPokemonsUseCase findAllPokemonsUseCase;
    private final UpdatePokemonUseCase updatePokemonUseCase;
    private final DeletePokemonUseCase deletePokemonUseCase;

    @PostMapping
    public ResponseEntity<PokemonResponse> create(@Valid @RequestBody CreatePokemonRequest request) {
        PokemonResponse response = createPokemonUseCase.execute(request, ExecutionContext.empty());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokemonResponse> findById(@PathVariable Long id) {
        PokemonResponse response = findPokemonUseCase.execute(id, ExecutionContext.empty());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PokemonResponse>> findAll() {
        List<PokemonResponse> response = findAllPokemonsUseCase.execute(null, ExecutionContext.empty());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PokemonResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePokemonRequest request) {
        PokemonResponse response = updatePokemonUseCase.execute(
                new UpdatePokemonUseCase.Input(id, request),
                ExecutionContext.empty()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deletePokemonUseCase.execute(id, ExecutionContext.empty());
        return ResponseEntity.noContent().build();
    }
}