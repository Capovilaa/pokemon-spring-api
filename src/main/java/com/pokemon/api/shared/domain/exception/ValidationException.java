package com.pokemon.api.shared.domain.exception;

public class ValidationException extends BusinessRuleException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
}
