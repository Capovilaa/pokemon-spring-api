package com.pokemon.api.type.infrastructure.web;

import com.pokemon.api.shared.application.usecase.ExecutionContext;
import com.pokemon.api.type.application.usecase.CreateTypeUseCase;
import com.pokemon.api.type.application.usecase.FindAllTypesUseCase;
import com.pokemon.api.type.infrastructure.web.dto.CreateTypeRequest;
import com.pokemon.api.type.infrastructure.web.dto.TypeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/types")
@RequiredArgsConstructor
public class TypeController {

    private final CreateTypeUseCase createTypeUseCase;
    private final FindAllTypesUseCase findAllTypesUseCase;

    @PostMapping
    public ResponseEntity<TypeResponse> create(@Valid @RequestBody CreateTypeRequest request) {
        TypeResponse response = createTypeUseCase.execute(request, ExecutionContext.empty());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TypeResponse>> findAll() {
        List<TypeResponse> response = findAllTypesUseCase.execute(null, ExecutionContext.empty());
        return ResponseEntity.ok(response);
    }
}