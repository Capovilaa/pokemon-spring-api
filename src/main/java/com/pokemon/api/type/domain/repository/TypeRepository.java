package com.pokemon.api.type.domain.repository;

import com.pokemon.api.type.domain.entity.TypeEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TypeRepository {

    TypeEntity save(TypeEntity type);

    Optional<TypeEntity> findById(Long id);

    Optional<TypeEntity> findByName(String name);

    List<TypeEntity> findAll();

    Set<TypeEntity> findAllByIdIn(Set<Long> ids);

    boolean existsByName(String name);
}