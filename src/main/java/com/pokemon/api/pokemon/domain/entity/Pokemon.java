package com.pokemon.api.pokemon.domain.entity;

import com.pokemon.api.trainer.domain.entity.Trainer;
import com.pokemon.api.type.domain.entity.TypeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pokemons")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer level;

    @Column(name = "species_id")
    private Integer speciesId;

    @Column(name = "base_hp")
    private Integer baseHp;

    @Column(name = "base_attack")
    private Integer baseAttack;

    @Column(name = "base_defense")
    private Integer baseDefense;

    @Column(name = "base_special_attack")
    private Integer baseSpecialAttack;

    @Column(name = "base_special_defense")
    private Integer baseSpecialDefense;

    @Column(name = "base_speed")
    private Integer baseSpeed;

    @Column(name = "sprite_url")
    private String spriteUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "pokemon_types",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private Set<TypeEntity> types;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}