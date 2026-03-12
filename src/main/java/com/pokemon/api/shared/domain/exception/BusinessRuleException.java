package com.pokemon.api.shared.domain.exception;

public abstract class BusinessRuleException extends RuntimeException {

    private final String errorCode;

    protected BusinessRuleException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}