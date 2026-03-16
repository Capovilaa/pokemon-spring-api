package com.pokemon.api.achievement.domain.entity;

import lombok.Getter;

@Getter
public enum AchievementType {
    FIRST_CATCH("First Catch", "Captured your first Pokémon!"),
    COLLECTOR("Collector", "Captured 10 Pokémons!"),
    DEDICATED("Dedicated Trainer", "Captured 50 Pokémons!"),
    POKEDEX_COMPLETE("Pokédex Complete", "Caught all 151 Kanto Pokémons!"),
    FIRST_BLOOD("First Blood", "Won your first battle!"),
    VETERAN("Veteran", "Won 5 battles!"),
    WARRIOR("Warrior", "Won 10 battles!"),
    FIRST_EVOLUTION("First Evolution", "Evolved a Pokémon for the first time!"),
    EVOLUTION_MASTER("Evolution Master", "Evolved 5 Pokémons!"),
    LEGENDARY_CATCHER("Legendary Catcher", "Caught a legendary Pokémon!");

    private final String title;
    private final String description;

    AchievementType(String title, String description) {
        this.title = title;
        this.description = description;
    }

}