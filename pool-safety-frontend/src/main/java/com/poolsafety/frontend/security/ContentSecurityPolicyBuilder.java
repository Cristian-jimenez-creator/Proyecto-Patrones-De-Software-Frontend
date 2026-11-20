package com.poolsafety.frontend.security;

public final class ContentSecurityPolicyBuilder {

    private ContentSecurityPolicyBuilder() {
    }

    /**
     * Same-origin policy. Images and the future live stream come only from this origin.
     * ASSUMPTION: Vaadin requires inline styles; the policy must be validated in a real browser.
     */
    public static String build(boolean relaxedForDevelopment) {
        String scriptSrc = relaxedForDevelopment ? "script-src 'self' 'unsafe-inline' 'unsafe-eval'" : "script-src 'self'";
        String connectSrc = relaxedForDevelopment ? "connect-src 'self' ws: wss:" : "connect-src 'self'";
        return String.join("; ",
                "default-src 'self'",
                scriptSrc,
                "style-src 'self' 'unsafe-inline'",
                "img-src 'self' data:",
                "font-src 'self'",
                connectSrc,
                "object-src 'none'",
                "base-uri 'self'",
                "form-action 'self'",
                "frame-ancestors 'none'");
    }
}
