package com.poolsafety.frontend.ui;

import java.util.List;
import java.util.Map;

import com.poolsafety.frontend.motion.Motion;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    private static final long serialVersionUID = 1L;

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("ps-login");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Posts to Spring Security (POST /login, CSRF-protected). The browser never calls the backend.
        // While submitting, the form disables itself; CSS shows a progress bar for [disabled].
        login.setAction("login");
        login.setForgotPasswordButtonVisible(true);
        login.addForgotPasswordListener(event -> UI.getCurrent().navigate(PasswordRecoveryView.class));
        login.setI18n(i18n("login.error.invalid"));

        H1 title = new H1(getTranslation("app.title"));
        title.addClassName("ps-login-title");
        Paragraph note = new Paragraph(getTranslation("login.security.note"));
        note.addClassName("ps-login-note");

        add(title, login, note);
        addAttachListener(event -> Motion.animateViewEnter(this));
    }

    @Override
    public String getPageTitle() {
        return getTranslation("page.login");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
        String errorKey = null;
        if (params.containsKey("error")) {
            errorKey = "login.error.invalid";
        } else if (params.containsKey("unavailable")) {
            errorKey = "login.error.unavailable";
        } else if (params.containsKey("ratelimited")) {
            errorKey = "login.error.ratelimited";
        } else if (params.containsKey("expired")) {
            errorKey = "login.expired";
        }
        if (errorKey != null) {
            login.setI18n(i18n(errorKey));
            login.setError(true);
        }
    }

    private LoginI18n i18n(String messageKeyPrefix) {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle(getTranslation("login.form.title"));
        i18n.getForm().setUsername(getTranslation("login.form.username"));
        i18n.getForm().setPassword(getTranslation("login.form.password"));
        i18n.getForm().setSubmit(getTranslation("login.form.submit"));
        i18n.getForm().setForgotPassword(getTranslation("login.form.forgot"));
        i18n.getErrorMessage().setTitle(getTranslation(messageKeyPrefix + ".title"));
        i18n.getErrorMessage().setMessage(getTranslation(messageKeyPrefix + ".message"));
        return i18n;
    }
}
