package com.pokemon.api.trainer.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "pokedex_entries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_id", "species_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokedexEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "species_id", nullable = false)
    private Integer speciesId;

    @Column(name = "species_name", nullable = false)
    private String speciesName;

    @Column(name = "sprite_url")
    private String spriteUrl;

    @Column(name = "caught_at", nullable = false, updatable = false)
    private LocalDateTime caughtAt;

    @PrePersist
    protected void onCreate() {
        caughtAt = LocalDateTime.now();
    }
}