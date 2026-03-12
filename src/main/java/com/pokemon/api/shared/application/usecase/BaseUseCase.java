package com.pokemon.api.shared.application.usecase;

public abstract class BaseUseCase<INPUT, OUTPUT> {

    public abstract OUTPUT execute(INPUT input, ExecutionContext context);
}