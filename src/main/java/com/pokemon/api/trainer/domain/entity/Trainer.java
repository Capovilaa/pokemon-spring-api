package com.pokemon.api.trainer.domain.entity;

import com.pokemon.api.pokemon.domain.entity.Pokemon;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", nullable = false, unique = true)
    private String keycloakId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pokemon> pokemons = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Column(name = "wins", nullable = false)
    @Builder.Default
    private Integer wins = 0;

    @Column(name = "losses", nullable = false)
    @Builder.Default
    private Integer losses = 0;

    @Column(name = "total_battles", nullable = false)
    @Builder.Default
    private Integer totalBattles = 0;

    @Column(name = "total_evolutions", nullable = false)
    @Builder.Default
    private Integer totalEvolutions = 0;
}