package com.smartpool.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, Environment env) throws Exception {
        boolean enabled = Boolean.parseBoolean(env.getProperty("SMARTPOOL_AUTH_ENABLED", "false"));
        if (!enabled) {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/login.html", "/assets/**", "/api/csrf", "/health", "/error").permitAll()
                            .anyRequest().authenticated())
                    .formLogin(form -> form.loginPage("/login.html")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/", true)
                            .failureUrl("/login.html?error")
                            .permitAll())
                    .logout(logout -> logout.logoutSuccessUrl("/login.html?loggedOut"));
        }
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(Environment env, PasswordEncoder encoder) {
        boolean enabled = Boolean.parseBoolean(env.getProperty("SMARTPOOL_AUTH_ENABLED", "false"));
        if (!enabled) return new InMemoryUserDetailsManager();

        String username = env.getProperty("SMARTPOOL_AUTH_USERNAME", "").trim();
        String password = env.getProperty("SMARTPOOL_AUTH_PASSWORD", "");
        if (username.isEmpty() || password.length() < 16) {
            throw new IllegalStateException("Set SMARTPOOL_AUTH_USERNAME and a SMARTPOOL_AUTH_PASSWORD of at least 16 characters.");
        }
        return new InMemoryUserDetailsManager(User.withUsername(username)
                .password(encoder.encode(password)).roles("ADMIN").build());
    }
}
