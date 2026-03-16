package com.pokemon.api.shared.infrastructure.pokeapi;

import java.util.Set;

public final class LegendarySpecies {

    private LegendarySpecies() {
    }

    public static final Set<Integer> IDS = Set.of(
            144, // Articuno
            145, // Zapdos
            146, // Moltres
            150, // Mewtwo
            151  // Mew
    );

    public static boolean isLegendary(Integer speciesId) {
        return speciesId != null && IDS.contains(speciesId);
    }
}