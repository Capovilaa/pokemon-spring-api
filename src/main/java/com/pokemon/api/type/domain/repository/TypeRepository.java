package com.pokemon.api.type.domain.repository;

import com.pokemon.api.type.domain.entity.TypeEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TypeRepository {
    List<TypeEntity> findAll();

    Set<TypeEntity> findAllByIdIn(Set<Long> ids);

    Optional<TypeEntity> findById(Long id);

    Optional<TypeEntity> findByName(String name);

    boolean existsByName(String name);

    TypeEntity save(TypeEntity type);
}