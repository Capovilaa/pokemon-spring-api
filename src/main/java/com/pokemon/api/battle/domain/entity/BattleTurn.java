package com.pokemon.api.battle.domain.entity;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "battle_turns")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleTurn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_id", nullable = false)
    private Battle battle;

    @Column(name = "turn_number", nullable = false)
    private Integer turnNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attacker_pokemon_id", nullable = false)
    private Pokemon attackerPokemon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "defender_pokemon_id", nullable = false)
    private Pokemon defenderPokemon;

    @Column(name = "move_name")
    private String moveName;

    @Column(name = "move_type")
    private String moveType;

    @Column(nullable = false)
    private Integer damage;

    @Column(name = "defender_hp_left", nullable = false)
    private Integer defenderHpLeft;

    @Column(name = "is_critical", nullable = false)
    @Builder.Default
    private Boolean isCritical = false;

    @Column(name = "effectiveness")
    private String effectiveness;
}