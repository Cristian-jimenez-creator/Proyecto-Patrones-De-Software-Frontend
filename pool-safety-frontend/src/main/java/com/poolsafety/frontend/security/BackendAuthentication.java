package com.poolsafety.frontend.security;

import java.time.Instant;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Authenticated session against the backend (Backend-for-Frontend pattern).
 * The token lives only in the server-side HTTP session: it is never sent to the browser.
 */
public final class BackendAuthentication extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final String accessToken;
    private final Instant expiresAt;

    public BackendAuthentication(String username, String accessToken, Instant expiresAt,
                                 Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
        setAuthenticated(true);
    }

    public String accessToken() {
        return accessToken;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    // Never include the token in logs
    @Override
    public String toString() {
        return "BackendAuthentication[user=" + username + "]";
    }
}
