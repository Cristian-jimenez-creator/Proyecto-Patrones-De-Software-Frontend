package com.poolsafety.frontend.security;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public Optional<String> username() {
        return authentication().map(Authentication::getName);
    }

    public Set<Role> roles() {
        Set<Role> roles = EnumSet.noneOf(Role.class);
        authentication().ifPresent(auth -> auth.getAuthorities()
                .forEach(a -> Role.fromBackend(a.getAuthority()).ifPresent(roles::add)));
        return roles;
    }

    private Optional<Authentication> authentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.of(auth);
    }
}
