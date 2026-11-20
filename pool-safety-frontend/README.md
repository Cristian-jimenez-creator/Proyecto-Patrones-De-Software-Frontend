# Pool Safety Frontend

Vaadin Flow 25 frontend (Java 21, Spring Boot 4.1) for the Pool Safety Monitoring Platform.
It follows the Backend-for-Frontend pattern: the browser talks only to this application, and only this
application talks to the backend API. Tokens, camera addresses and credentials never reach the browser.

## Status: step 1
Theme, CSS animation system, TypeScript motion helpers, security configuration, main layout,
`LoginView` and `PasswordRecoveryView`. `DashboardView` is a placeholder that shows no data.

## Language rules
Code, tests, logs and technical documentation are in English. User-facing text is Spanish and lives in
`src/main/resources/vaadin-i18n/translations.properties` (English keys, Spanish values).

## Assumptions to confirm (the backend does not define these yet)
- Login contract: `POST /api/v1/auth/login` `{username, password}` returns `{accessToken, expiresIn, user{username, roles[]}}`.
- Roles are `ADMIN`, `SUPERVISOR`, `OPERATOR`. The menu matrix is in `NavigationAccess`.
- Spring Boot `4.1.0` and Vaadin `25.0.0` were not verified against Maven Central.
- Vaadin 25 API details used and not verified: `@Theme`, `VaadinSecurityConfigurer`, `DefaultI18NProvider` picking up
  `vaadin-i18n/translations.properties`.
- The CSP in `ContentSecurityPolicyBuilder` must be validated in a real browser.
- Login progress bar relies on the `disabled` attribute of `vaadin-login-form`; verify visually.
- The `Atkinson Hyperlegible` font is listed in the CSS stack but not bundled.

## Not implemented yet
- Session-expiry warning before timeout (planned for step 7). Expiry already redirects to `/login?expired`.
- Password recovery submission: the form is disabled until the backend exposes an endpoint.

## Run
```bash
export BACKEND_BASE_URL=http://localhost:8080
mvn spring-boot:run -Dspring-boot.run.profiles=dev     # http://localhost:8081
```
Production uses the `prod` profile behind HTTPS (Secure cookies, strict CSP).

## Test
```bash
mvn test
```
See `docs/MANUAL_TESTS.md` for manual checks, including a real MJPEG phone webcam.
