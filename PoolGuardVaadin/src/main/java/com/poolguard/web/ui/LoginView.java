package com.poolguard.web.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "login", autoLayout = false)
@PageTitle("Sign in | PoolGuard")
public final class LoginView extends VerticalLayout {
    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Div emblem = new Div(new Span("P"));
        emblem.addClassName("login-emblem");
        H1 title = new H1("PoolGuard");
        title.addClassName("login-title");
        Paragraph subtitle = new Paragraph("A calmer way to keep watch.");
        subtitle.addClassName("login-subtitle");
        LoginForm loginForm = new LoginForm();
        loginForm.addClassName("poolguard-login-form");
        loginForm.setAction("login");
        Div card = new Div(emblem, title, subtitle, loginForm,
                new Paragraph("SECURE ACCESS  ·  POOL SAFETY MONITORING"));
        card.addClassName("login-card");
        add(card);
    }
}
