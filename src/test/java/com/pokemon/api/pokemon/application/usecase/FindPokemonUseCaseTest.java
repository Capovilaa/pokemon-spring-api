package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonMapper;
import com.pokemon.api.pokemon.infrastructure.web.dto.PokemonResponse;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.infrastructure.pokeapi.EvolutionService;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindPokemonUseCaseTest {

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private PokemonMapper pokemonMapper;

    @Mock
    private EvolutionService evolutionService;

    @InjectMocks
    private FindPokemonUseCase useCase;

    @Test
    void shouldReturnPokemonWhenFound() {
        Pokemon pokemon = Pokemon.builder().id(1L).name("Charizard").level(50).build();

        PokemonResponse fakeResponse = PokemonResponse.builder().id(1L).name("Charizard").level(50).build();

        when(pokemonRepository.findById(1L)).thenReturn(Optional.of(pokemon));
        when(evolutionService.getNextEvolution("Charizard")).thenReturn(Optional.empty());
        when(pokemonMapper.toResponse(pokemon, null)).thenReturn(fakeResponse);

        PokemonResponse result = useCase.execute(1L, ExecutionContext.empty());

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Charizard");
    }

    @Test
    void shouldThrowNotFoundWhenPokemonDoesNotExist() {
        when(pokemonRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, ExecutionContext.empty()))
                .isInstanceOf(NotFoundException.class);
    }

}