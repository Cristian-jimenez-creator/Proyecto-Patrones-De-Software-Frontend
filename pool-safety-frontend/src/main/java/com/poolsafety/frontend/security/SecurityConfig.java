package com.poolsafety.frontend.security;

import com.poolsafety.frontend.ui.LoginView;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

/**
 * CSRF protection stays enabled (Spring Security default, integrated with Vaadin).
 * ASSUMPTION: VaadinSecurityConfigurer is the Vaadin 25 entry point.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityProperties properties) throws Exception {
        http.headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                        ContentSecurityPolicyBuilder.build(properties.relaxedCspForDevelopment())))
                .referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.NO_REFERRER)));
        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> configurer.loginView(LoginView.class));
        http.formLogin(form -> form.failureHandler(new LoginFailureHandler()));
        return http.build();
    }
}
