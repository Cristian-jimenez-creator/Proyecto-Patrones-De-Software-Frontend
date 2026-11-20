package com.poolsafety.frontend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServiceUnavailable;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/** Tests HTTP response mapping for login. It involves no camera and simulates no camera behavior. */
class BackendAuthenticationProviderTest {

    private static final String URL = "http://backend.test/api/v1/auth/login";

    private MockRestServiceServer server;
    private BackendAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://backend.test");
        server = MockRestServiceServer.bindTo(builder).build();
        provider = new BackendAuthenticationProvider(builder.build());
    }

    private Authentication attempt() {
        return provider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated("ana", "secret"));
    }

    @Test
    void successMapsRolesAndKeepsTokenServerSide() {
        server.expect(requestTo(URL)).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"accessToken":"tok-123","expiresIn":900,
                         "user":{"username":"ana","roles":["OPERATOR","ROLE_ADMIN","UNKNOWN"]},"extra":1}
                        """, MediaType.APPLICATION_JSON));

        Authentication result = attempt();

        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getAuthorities()).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_OPERATOR", "ROLE_ADMIN");
        assertThat(((BackendAuthentication) result).accessToken()).isEqualTo("tok-123");
        assertThat(result.toString()).doesNotContain("tok-123");
    }

    @Test
    void unauthorizedBecomesBadCredentials() {
        server.expect(requestTo(URL)).andRespond(withUnauthorizedRequest());
        assertThatThrownBy(this::attempt).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void tooManyRequestsBecomesRateLimited() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThatThrownBy(this::attempt).isInstanceOf(LoginRateLimitedException.class);
    }

    @Test
    void serviceUnavailableBecomesBackendUnavailable() {
        server.expect(requestTo(URL)).andRespond(withServiceUnavailable());
        assertThatThrownBy(this::attempt).isInstanceOf(BackendUnavailableException.class);
    }

    @Test
    void rawBackendExceptionNeverLeaks() {
        server.expect(requestTo(URL)).andRespond(withServerError()
                .body("java.lang.NullPointerException at com.secret.Internal(db=10.0.0.5)"));
        assertThatThrownBy(this::attempt)
                .isInstanceOf(BackendUnavailableException.class)
                .hasMessageNotContaining("NullPointer")
                .hasMessageNotContaining("10.0.0.5");
    }

    @Test
    void userWithoutSupportedRoleIsRejected() {
        server.expect(requestTo(URL)).andRespond(withSuccess("""
                {"accessToken":"t","expiresIn":60,"user":{"username":"ana","roles":["GUEST"]}}
                """, MediaType.APPLICATION_JSON));
        assertThatThrownBy(this::attempt).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void blankPasswordNeverReachesBackend() {
        assertThatThrownBy(() -> provider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated("ana", "")))
                .isInstanceOf(BadCredentialsException.class);
        server.verify();
    }
}
