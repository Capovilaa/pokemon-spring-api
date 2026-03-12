package com.pokemon.api.type.infrastructure.persistence;

import com.pokemon.api.type.domain.entity.TypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SpringTypeRepository extends JpaRepository<TypeEntity, Long> {

    Optional<TypeEntity> findByName(String name);

    boolean existsByName(String name);

    Set<TypeEntity> findAllByIdIn(Set<Long> ids);
}