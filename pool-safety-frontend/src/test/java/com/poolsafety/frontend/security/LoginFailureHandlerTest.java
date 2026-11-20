package com.poolsafety.frontend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

class LoginFailureHandlerTest {

    private String redirectFor(AuthenticationException ex) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        new LoginFailureHandler().onAuthenticationFailure(new MockHttpServletRequest(), response, ex);
        return response.getRedirectedUrl();
    }

    @Test
    void mapsEachFailureToAFixedParameter() throws Exception {
        assertThat(redirectFor(new BadCredentialsException("x"))).isEqualTo("/login?error");
        assertThat(redirectFor(new LoginRateLimitedException())).isEqualTo("/login?ratelimited");
        assertThat(redirectFor(new BackendUnavailableException())).isEqualTo("/login?unavailable");
    }
}
