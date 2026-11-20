package com.poolguard.web.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | PoolGuard")
public final class DashboardView extends VerticalLayout {
    public DashboardView() {
        addClassName("view"); add(new H1("Safety dashboard"));
        Div status = new Div(); status.addClassNames("risk-card", "risk-waiting"); status.setText("Waiting for real backend data");
        add(status, new Paragraph("Live camera and risk information appear only after the backend returns verified data."));
    }
}
