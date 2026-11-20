package com.poolguard.web.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouterLink;

public final class MainLayout extends AppLayout {
    public MainLayout() {
        H2 brand = new H2("POOLGUARD"); brand.addClassName("brand");
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), brand); header.addClassName("app-header"); addToNavbar(header);
        SideNav nav = new SideNav(); nav.addItem(new SideNavItem("Dashboard", DashboardView.class)); nav.addItem(new SideNavItem("Cameras", CamerasView.class));
        addToDrawer(nav);
    }
}
