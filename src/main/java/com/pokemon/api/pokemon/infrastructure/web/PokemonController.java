package com.pokemon.api.pokemon.infrastructure.web;

import com.pokemon.api.pokemon.application.usecase.*;
import com.pokemon.api.pokemon.infrastructure.web.dto.CreatePokemonRequest;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.pokemon.infrastructure.web.dto.UpdatePokemonRequest;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.security.AuthenticatedUser;
import com.pokemon.api.shared.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pokémons", description = "Manage your Pokémon")
@RestController
@RequestMapping("/api/v1/pokemons")
@RequiredArgsConstructor
public class PokemonController {

    private final CreatePokemonUseCase createPokemonUseCase;
    private final FindPokemonUseCase findPokemonUseCase;
    private final FindAllPokemonsUseCase findAllPokemonsUseCase;
    private final UpdatePokemonUseCase updatePokemonUseCase;
    private final DeletePokemonUseCase deletePokemonUseCase;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pokémon captured successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Pokémon species not found in PokéAPI")
    })
    @Operation(summary = "Capture a Pokémon", description = "Fetches real data from PokéAPI and saves to trainer's collection")
    @PostMapping
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<PokemonResponse> create(@Valid @RequestBody CreatePokemonRequest request) {
        AuthenticatedUser user = SecurityUtils.getAuthenticatedUser();
        PokemonResponse response = createPokemonUseCase.execute(request, ExecutionContext.of(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pokémon found"),
            @ApiResponse(responseCode = "404", description = "Pokémon not found")
    })
    @Operation(summary = "Find an specific Pokémon", description = "Get information from a specific Pokémon by its id")
    @GetMapping("/{id}")
    public ResponseEntity<PokemonResponse> findById(@PathVariable Long id) {
        PokemonResponse response = findPokemonUseCase.execute(id, ExecutionContext.empty());
        return ResponseEntity.ok(response);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all Pokémon")
    })
    @Operation(summary = "Find all Pokémon", description = "Get all already gotten Pokémon from any trainer")
    @GetMapping
    public ResponseEntity<List<PokemonResponse>> findAll() {
        List<PokemonResponse> response = findAllPokemonsUseCase.execute(null, ExecutionContext.empty());
        return ResponseEntity.ok(response);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pokémon updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Pokémon not found"),
            @ApiResponse(responseCode = "403", description = "Not allowed to update this Pokémon")
    })
    @Operation(summary = "Update Pokémon", description = "Updates a specific Pokémon by its id")
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

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pokémon deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Pokémon not found"),
            @ApiResponse(responseCode = "403", description = "Not allowed to delete this Pokémon")
    })
    @Operation(summary = "Delete Pokémon", description = "Deletes a Pokémon by its id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deletePokemonUseCase.execute(id, ExecutionContext.empty());
        return ResponseEntity.noContent().build();
    }
}