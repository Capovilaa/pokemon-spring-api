package com.pokemon.api.shared.domain.exception;

public class NotFoundException extends BusinessRuleException {

    public NotFoundException(String resource, Object identifier) {
        super(
                String.format("%s not found with identifier: %s", resource, identifier),
                "NOT_FOUND"
        );
    }

    public NotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
}