package com.pokemon.api.type.application.usecase;

import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.shared.domain.exception.ValidationException;
import com.pokemon.api.type.domain.entity.TypeEntity;
import com.pokemon.api.type.domain.repository.TypeRepository;
import com.pokemon.api.type.infrastructure.web.dto.CreateTypeRequest;
import com.pokemon.api.type.infrastructure.web.dto.TypeMapper;
import com.pokemon.api.type.infrastructure.web.dto.TypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTypeUseCase extends BaseUseCase<CreateTypeRequest, TypeResponse> {

    private final TypeRepository typeRepository;
    private final TypeMapper typeMapper;

    @Override
    public TypeResponse execute(CreateTypeRequest input, ExecutionContext context) {
        if (typeRepository.existsByName(input.name())) {
            throw new ValidationException("Type '" + input.name() + "' already exists");
        }

        TypeEntity type = TypeEntity.builder()
                .name(input.name())
                .build();

        TypeEntity saved = typeRepository.save(type);

        return typeMapper.toResponse(saved);
    }
}