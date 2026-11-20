package com.poolsafety.frontend.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/** Maps the failure type to a fixed query parameter; exception details never reach the browser. */
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String target;
        if (exception instanceof LoginRateLimitedException) {
            target = "/login?ratelimited";
        } else if (exception instanceof BackendUnavailableException) {
            target = "/login?unavailable";
        } else {
            target = "/login?error";
        }
        response.sendRedirect(request.getContextPath() + target);
    }
}
