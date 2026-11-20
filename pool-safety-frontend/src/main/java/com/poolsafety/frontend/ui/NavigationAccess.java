package com.poolsafety.frontend.ui;

import java.util.List;
import java.util.Set;

import com.poolsafety.frontend.security.Role;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Navigation entries and role visibility, for usability only: the backend remains the final
 * authorization boundary and every view also carries its own @RolesAllowed annotation.
 * An entry is rendered only when its view already exists (implemented).
 * ASSUMPTION: this role matrix is provisional until the backend defines permissions.
 */
public final class NavigationAccess {

    public record Entry(String labelKey, String route, VaadinIcon icon, Set<Role> roles, boolean implemented) {
    }

    private static final Set<Role> ALL = Set.of(Role.ADMIN, Role.SUPERVISOR, Role.OPERATOR);
    private static final Set<Role> MANAGEMENT = Set.of(Role.ADMIN, Role.SUPERVISOR);
    private static final Set<Role> ADMIN_ONLY = Set.of(Role.ADMIN);

    private static final List<Entry> ENTRIES = List.of(
            new Entry("nav.dashboard", "", VaadinIcon.DASHBOARD, ALL, true),
            new Entry("nav.pools", "pools", VaadinIcon.WATER, ALL, false),
            new Entry("nav.cameras", "cameras", VaadinIcon.CAMERA, ALL, false),
            new Entry("nav.alerts", "alerts", VaadinIcon.BELL, ALL, false),
            new Entry("nav.incidents", "incidents", VaadinIcon.WARNING, ALL, false),
            new Entry("nav.reports", "reports", VaadinIcon.FILE_TEXT_O, MANAGEMENT, false),
            new Entry("nav.users", "users", VaadinIcon.USERS, ADMIN_ONLY, false));

    private NavigationAccess() {
    }

    /** Every planned entry the roles may see, whether or not its view exists yet. */
    public static List<Entry> plannedFor(Set<Role> roles) {
        return ENTRIES.stream().filter(e -> e.roles().stream().anyMatch(roles::contains)).toList();
    }

    /** Entries actually rendered in the menu. */
    public static List<Entry> visibleFor(Set<Role> roles) {
        return plannedFor(roles).stream().filter(Entry::implemented).toList();
    }
}
