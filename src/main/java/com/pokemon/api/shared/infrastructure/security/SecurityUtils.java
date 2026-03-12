package com.pokemon.api.shared.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static AuthenticatedUser getAuthenticatedUser() {
        Jwt jwt = (Jwt) Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getPrincipal();

        assert jwt != null;
        return new AuthenticatedUser(
                jwt.getSubject(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email"),
                extractRoles(jwt)
        );
    }

    @SuppressWarnings("unchecked")
    private static List<String> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return List.of();
        }
        return (List<String>) realmAccess.get("roles");
    }
}