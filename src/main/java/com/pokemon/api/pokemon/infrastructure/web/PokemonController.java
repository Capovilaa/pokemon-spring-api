package com.pokemon.api.pokemon.infrastructure.web;

import com.pokemon.api.pokemon.application.usecase.*;
import com.pokemon.api.pokemon.infrastructure.web.dto.CreatePokemonRequest;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.pokemon.infrastructure.web.dto.UpdatePokemonRequest;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.security.AuthenticatedUser;
import com.pokemon.api.shared.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<PokemonResponse> create(@Valid @RequestBody CreatePokemonRequest request) {
        AuthenticatedUser user = SecurityUtils.getAuthenticatedUser();
        PokemonResponse response = createPokemonUseCase.execute(request, ExecutionContext.of(user));
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
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<PokemonResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePokemonRequest request) {
        AuthenticatedUser user = SecurityUtils.getAuthenticatedUser();
        PokemonResponse response = updatePokemonUseCase.execute(
                new UpdatePokemonUseCase.Input(id, request),
                ExecutionContext.of(user)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deletePokemonUseCase.execute(id, ExecutionContext.empty());
        return ResponseEntity.noContent().build();
    }
}