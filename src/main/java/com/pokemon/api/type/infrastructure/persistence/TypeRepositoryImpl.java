package com.pokemon.api.type.infrastructure.persistence;

import com.pokemon.api.type.domain.entity.TypeEntity;
import com.pokemon.api.type.domain.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TypeRepositoryImpl implements TypeRepository {

    private final SpringTypeRepository springTypeRepository;

    @Override
    public TypeEntity save(TypeEntity type) {
        return springTypeRepository.save(type);
    }

    @Override
    public Optional<TypeEntity> findById(Long id) {
        return springTypeRepository.findById(id);
    }

    @Override
    public Optional<TypeEntity> findByName(String name) {
        return springTypeRepository.findByName(name);
    }

    @Override
    public List<TypeEntity> findAll() {
        return springTypeRepository.findAll();
    }

    @Override
    public Set<TypeEntity> findAllByIdIn(Set<Long> ids) {
        return springTypeRepository.findAllByIdIn(ids);
    }

    @Override
    public boolean existsByName(String name) {
        return springTypeRepository.existsByName(name);
    }
}