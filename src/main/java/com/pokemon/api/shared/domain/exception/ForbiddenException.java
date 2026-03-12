package com.pokemon.api.shared.domain.exception;

public class ForbiddenException extends BusinessRuleException {

    public ForbiddenException(String message) {
        super(message, "FORBIDDEN");
    }
}