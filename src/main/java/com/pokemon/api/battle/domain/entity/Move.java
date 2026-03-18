package com.pokemon.api.battle.domain.entity;

public record Move(
        String name,
        String type,
        Integer power,
        Integer accuracy,
        String damageClass
) {
    public boolean isUsable() {
        return power != null && power > 0;
    }
}