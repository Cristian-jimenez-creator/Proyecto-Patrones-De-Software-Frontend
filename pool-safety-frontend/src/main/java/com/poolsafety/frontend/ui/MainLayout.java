package com.poolsafety.frontend.ui;

import com.poolsafety.frontend.security.CurrentUser;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.security.AuthenticationContext;

/** Desktop: persistent side rail. Mobile: the drawer becomes a compact overlay (Vaadin AppLayout). */
public class MainLayout extends AppLayout {

    private static final long serialVersionUID = 1L;

    public MainLayout(AuthenticationContext authContext, CurrentUser currentUser) {
        addClassName("ps-app");
        setPrimarySection(Section.DRAWER);

        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel(getTranslation("nav.toggle.label"));

        H1 title = new H1(getTranslation("app.title"));
        title.addClassName("ps-app-title");
        addToNavbar(toggle, title);

        SideNav nav = new SideNav();
        nav.getElement().setAttribute("aria-label", getTranslation("nav.main.label"));
        NavigationAccess.visibleFor(currentUser.roles())
                .forEach(entry -> nav.addItem(new SideNavItem(
                        getTranslation(entry.labelKey()), entry.route(), entry.icon().create())));

        Span user = new Span(currentUser.username().orElse(""));
        user.addClassName("ps-app-user");
        Button logout = new Button(getTranslation("layout.logout"), VaadinIcon.SIGN_OUT.create(),
                event -> authContext.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout footer = new HorizontalLayout(user, logout);
        footer.setWidthFull();
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        footer.addClassName("ps-app-footer");

        addToDrawer(new Scroller(nav), footer);
    }
}
