package com.pokemon.api.pokemon.application.usecase;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.pokemon.domain.repository.PokemonRepository;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletePokemonUseCaseTest {

    @Mock
    private PokemonRepository pokemonRepository;

    @InjectMocks
    private DeletePokemonUseCase useCase;

    @Test
    void shouldDeletePokemonWhenFound() {
        Pokemon pokemon = Pokemon.builder().id(1L).name("Charizard").level(50).build();

        when(pokemonRepository.findById(1L)).thenReturn(Optional.of(pokemon));

        useCase.execute(1L, ExecutionContext.empty());

        verify(pokemonRepository).deleteById(1L);
    }

    @Test
    void shouldThrowNotFoundWhenPokemonDoesNotExist() {
        when(pokemonRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, ExecutionContext.empty()))
                .isInstanceOf(NotFoundException.class);
    }
}