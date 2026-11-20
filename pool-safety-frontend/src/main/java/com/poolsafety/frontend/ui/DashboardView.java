package com.poolsafety.frontend.ui;

import com.poolsafety.frontend.motion.Motion;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

/** Step 1 placeholder: shows no data. The real dashboard is built in step 3. */
@Route(value = "", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "SUPERVISOR", "OPERATOR"})
public class DashboardView extends VerticalLayout implements HasDynamicTitle {

    private static final long serialVersionUID = 1L;

    public DashboardView() {
        add(new H2(getTranslation("dashboard.title")), new Paragraph(getTranslation("dashboard.empty")));
        addAttachListener(event -> Motion.animateViewEnter(this));
    }

    @Override
    public String getPageTitle() {
        return getTranslation("page.dashboard");
    }
}
