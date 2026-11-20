package com.poolsafety.frontend.ui;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import com.poolsafety.frontend.security.Role;

import org.junit.jupiter.api.Test;

class NavigationAccessTest {

    private static List<String> routes(Set<Role> roles) {
        return NavigationAccess.plannedFor(roles).stream().map(NavigationAccess.Entry::route).toList();
    }

    @Test
    void adminSeesEveryPlannedEntry() {
        assertThat(routes(Set.of(Role.ADMIN)))
                .containsExactly("", "pools", "cameras", "alerts", "incidents", "reports", "users");
    }

    @Test
    void supervisorSeesReportsButNotUsers() {
        assertThat(routes(Set.of(Role.SUPERVISOR))).contains("reports").doesNotContain("users");
    }

    @Test
    void operatorSeesNeitherReportsNorUsers() {
        assertThat(routes(Set.of(Role.OPERATOR))).contains("cameras", "alerts", "incidents")
                .doesNotContain("reports", "users");
    }

    @Test
    void noRolesMeansNoEntries() {
        assertThat(NavigationAccess.plannedFor(Set.of())).isEmpty();
        assertThat(NavigationAccess.visibleFor(Set.of())).isEmpty();
    }

    @Test
    void onlyImplementedViewsAreRendered() {
        assertThat(NavigationAccess.visibleFor(Set.of(Role.ADMIN)))
                .extracting(NavigationAccess.Entry::route)
                .containsExactly("");
    }
}
