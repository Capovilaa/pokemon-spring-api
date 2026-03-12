package com.pokemon.api.type.application.usecase;

import com.pokemon.api.shared.application.usecase.BaseUseCase;
import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.type.domain.repository.TypeRepository;
import com.pokemon.api.type.infrastructure.web.dto.TypeMapper;
import com.pokemon.api.type.infrastructure.web.dto.TypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindAllTypesUseCase extends BaseUseCase<Void, List<TypeResponse>> {

    private final TypeRepository typeRepository;
    private final TypeMapper typeMapper;

    @Override
    public List<TypeResponse> execute(Void input, ExecutionContext context) {
        return typeRepository.findAll()
                .stream()
                .map(typeMapper::toResponse)
                .toList();
    }
}