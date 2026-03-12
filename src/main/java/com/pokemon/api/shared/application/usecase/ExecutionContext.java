package com.pokemon.api.shared.application.usecase;

import com.pokemon.api.shared.infrastructure.security.AuthenticatedUser;

public record ExecutionContext(
        String requestId,
        String userAgent,
        AuthenticatedUser user
) {
    public static ExecutionContext empty() {
        return new ExecutionContext("anonymous", "unknown", null);
    }

    public static ExecutionContext of(AuthenticatedUser user) {
        return new ExecutionContext("anonymous", "unknown", user);
    }
}