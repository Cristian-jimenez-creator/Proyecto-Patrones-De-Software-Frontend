package com.poolsafety.frontend.security;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Validates credentials against the backend, which stays the source of truth for users and roles.
 * ASSUMPTION (the backend has no login endpoint yet):
 * POST /api/v1/auth/login {username, password} -> {accessToken, expiresIn, user{username, roles[]}}.
 */
@Component
public class BackendAuthenticationProvider implements AuthenticationProvider {

    static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final Logger log = LoggerFactory.getLogger(BackendAuthenticationProvider.class);

    private final RestClient backend;

    public BackendAuthenticationProvider(RestClient backendRestClient) {
        this.backend = backendRestClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        Object rawPassword = authentication.getCredentials();
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.toString().isEmpty()) {
            throw new BadCredentialsException("invalid-credentials");
        }

        LoginResponse response = callBackend(username, rawPassword.toString());

        if (response == null || response.accessToken() == null || response.accessToken().isBlank()
                || response.user() == null || response.user().roles() == null) {
            log.warn("Backend login response was incomplete");
            throw new BackendUnavailableException();
        }

        List<SimpleGrantedAuthority> authorities = response.user().roles().stream()
                .map(Role::fromBackend)
                .flatMap(Optional::stream)
                .distinct()
                .map(role -> new SimpleGrantedAuthority(role.authority()))
                .toList();
        if (authorities.isEmpty()) {
            throw new BadCredentialsException("no-supported-role");
        }

        Instant expiresAt = Instant.now().plus(Duration.ofSeconds(Math.max(response.expiresIn(), 0)));
        return new BackendAuthentication(username, response.accessToken(), expiresAt, authorities);
    }

    private LoginResponse callBackend(String username, String password) {
        try {
            return backend.post()
                    .uri(LOGIN_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new LoginRequest(username, password))
                    .retrieve()
                    .body(LoginResponse.class);
        } catch (RestClientResponseException ex) {
            int status = ex.getStatusCode().value();
            // Log only the status code: never the body or the credentials
            log.warn("Backend login rejected with status {}", status);
            if (status == 400 || status == 401 || status == 403 || status == 422) {
                throw new BadCredentialsException("invalid-credentials");
            }
            if (status == 429) {
                throw new LoginRateLimitedException();
            }
            throw new BackendUnavailableException();
        } catch (RestClientException ex) {
            log.warn("Backend login unreachable: {}", ex.getClass().getSimpleName());
            throw new BackendUnavailableException();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    record LoginRequest(String username, String password) {
        @Override
        public String toString() {
            return "LoginRequest[username=" + username + "]";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record LoginResponse(String accessToken, long expiresIn, UserInfo user) {
        @Override
        public String toString() {
            return "LoginResponse[user=" + user + "]";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record UserInfo(String username, List<String> roles) {
    }
}
