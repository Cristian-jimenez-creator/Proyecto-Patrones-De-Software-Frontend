package com.smartpool.backend.controller;

import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class CsrfController {
    @GetMapping("/api/csrf")
    public Object csrf(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token == null) return Map.of();
        return Map.of("token", token.getToken(), "headerName", token.getHeaderName(), "parameterName", token.getParameterName());
    }
}
