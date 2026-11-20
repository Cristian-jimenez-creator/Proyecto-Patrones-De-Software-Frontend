package com.poolsafety.frontend.security;

import org.springframework.security.core.AuthenticationException;

public class LoginRateLimitedException extends AuthenticationException {
    private static final long serialVersionUID = 1L;

    public LoginRateLimitedException() {
        super("login-rate-limited");
    }
}
