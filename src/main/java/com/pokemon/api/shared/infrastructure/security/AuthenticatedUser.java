package com.pokemon.api.shared.infrastructure.security;

import java.util.List;

public record AuthenticatedUser(
        String id,
        String username,
        String email,
        List<String> roles
) {
    public boolean isAdmin() {
        return roles.contains("ADMIN");
    }

    public boolean isTrainer() {
        return roles.contains("TRAINER");
    }
}