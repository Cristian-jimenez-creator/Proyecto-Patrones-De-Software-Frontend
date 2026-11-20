package com.poolsafety.frontend.security;

import org.springframework.security.authentication.AuthenticationServiceException;

public class BackendUnavailableException extends AuthenticationServiceException {
    private static final long serialVersionUID = 1L;

    public BackendUnavailableException() {
        super("backend-unavailable");
    }
}
