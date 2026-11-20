package com.poolguard.web.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import jakarta.annotation.security.PermitAll;

@PermitAll
public final class MainLayout extends AppLayout {
    public MainLayout() {
        addClassName("poolguard-shell");

        DrawerToggle drawerToggle = new DrawerToggle();
        drawerToggle.addClassName("drawer-toggle");
        Div breadcrumb = new Div(new Span("POOL SAFETY"), new Span("/"), new Span("Live workspace"));
        breadcrumb.addClassName("topbar-breadcrumb");
        Span session = new Span("Protected session");
        session.addClassName("session-badge");
        HorizontalLayout topbar = new HorizontalLayout(drawerToggle, breadcrumb, session);
        topbar.addClassName("app-header");
        topbar.setWidthFull();
        topbar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        addToNavbar(topbar);

        Div brandMark = new Div(new Span("P"));
        brandMark.addClassName("brand-mark");
        H2 brand = new H2("PoolGuard");
        brand.addClassName("brand");
        Paragraph descriptor = new Paragraph("POOL SAFETY MONITOR");
        descriptor.addClassName("brand-caption");
        Div brandBlock = new Div(brandMark, new Div(brand, descriptor));
        brandBlock.addClassName("brand-block");

        Paragraph sectionLabel = new Paragraph("WORKSPACE");
        sectionLabel.addClassName("nav-caption");
        SideNav nav = new SideNav();
        nav.addClassName("main-nav");
        nav.addItem(new SideNavItem("Overview", DashboardView.class));
        nav.addItem(new SideNavItem("Live cameras", CamerasView.class));
        nav.addItem(new SideNavItem("Connect a camera", AddCameraView.class));

        Div drawerFooter = new Div(new Span("●"), new Span("Camera network"), new Span("Managed locally"));
        drawerFooter.addClassName("drawer-footer");
        addToDrawer(brandBlock, sectionLabel, nav, drawerFooter);
        setPrimarySection(Section.DRAWER);
    }
}
