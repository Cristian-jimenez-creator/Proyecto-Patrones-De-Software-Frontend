package com.poolguard.web.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "cameras", layout = MainLayout.class)
@PageTitle("Cameras | PoolGuard")
public final class CamerasView extends VerticalLayout {
    public CamerasView() {
        addClassName("view"); add(new H1("IP cameras"), new Paragraph("Register a real camera stream through the secured backend."));
        Button add = new Button("Add IP camera"); add.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("cameras/add"))); add(add);
    }
}
