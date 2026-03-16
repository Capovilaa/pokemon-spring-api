package com.pokemon.api.battle.domain.entity;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import com.pokemon.api.trainer.domain.entity.Trainer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "battles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Battle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attacker_trainer_id", nullable = false)
    private Trainer attackerTrainer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "defender_trainer_id", nullable = false)
    private Trainer defenderTrainer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attacker_pokemon_id", nullable = false)
    private Pokemon attackerPokemon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "defender_pokemon_id", nullable = false)
    private Pokemon defenderPokemon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_trainer_id")
    private Trainer winnerTrainer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_pokemon_id")
    private Pokemon winnerPokemon;

    @Column(name = "total_turns", nullable = false)
    private Integer totalTurns;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("turnNumber ASC")
    @Builder.Default
    private List<BattleTurn> turns = new ArrayList<>();

    @Column(name = "fought_at", nullable = false, updatable = false)
    private LocalDateTime foughtAt;

    @PrePersist
    protected void onCreate() {
        foughtAt = LocalDateTime.now();
    }
}