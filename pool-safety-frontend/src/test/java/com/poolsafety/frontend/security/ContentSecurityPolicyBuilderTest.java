package com.poolsafety.frontend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentSecurityPolicyBuilderTest {

    @Test
    void productionPolicyIsSameOriginOnly() {
        String csp = ContentSecurityPolicyBuilder.build(false);
        assertThat(csp).contains("default-src 'self'", "script-src 'self'", "frame-ancestors 'none'",
                "object-src 'none'", "form-action 'self'");
        assertThat(csp).doesNotContain("unsafe-eval").doesNotContain("ws:").doesNotContain("*");
        assertThat(csp).doesNotContain("http://").doesNotContain("https://");
    }

    @Test
    void developmentPolicyIsRelaxedOnlyWhenRequested() {
        assertThat(ContentSecurityPolicyBuilder.build(true)).contains("unsafe-eval");
        assertThat(ContentSecurityPolicyBuilder.build(false)).doesNotContain("unsafe-eval");
    }
}
