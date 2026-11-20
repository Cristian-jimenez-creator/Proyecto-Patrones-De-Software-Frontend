package com.poolsafety.frontend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void parsesPlainAndPrefixedNamesIgnoringCase() {
        assertThat(Role.fromBackend("admin")).contains(Role.ADMIN);
        assertThat(Role.fromBackend("ROLE_SUPERVISOR")).contains(Role.SUPERVISOR);
        assertThat(Role.fromBackend(" operator ")).contains(Role.OPERATOR);
    }

    @Test
    void unknownOrNullIsEmpty() {
        assertThat(Role.fromBackend("VIEWER")).isEmpty();
        assertThat(Role.fromBackend(null)).isEmpty();
    }
}
