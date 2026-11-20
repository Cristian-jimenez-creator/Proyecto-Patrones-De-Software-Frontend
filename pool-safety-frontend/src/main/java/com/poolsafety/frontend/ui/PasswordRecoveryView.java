package com.poolsafety.frontend.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Step 1 renders the form and validates the email locally. Submission is intentionally disabled:
 * the backend has no recovery endpoint yet, and the view must not pretend that an email was sent.
 */
@Route("password-recovery")
@AnonymousAllowed
public class PasswordRecoveryView extends VerticalLayout implements HasDynamicTitle {

    private static final long serialVersionUID = 1L;

    public PasswordRecoveryView() {
        addClassName("ps-login");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        EmailField email = new EmailField(getTranslation("recovery.email"));
        email.setRequiredIndicatorVisible(true);
        email.setErrorMessage(getTranslation("recovery.email.invalid"));
        email.setWidthFull();

        Button submit = new Button(getTranslation("recovery.submit"));
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submit.setEnabled(false);

        Paragraph unavailable = new Paragraph(getTranslation("recovery.unavailable"));
        unavailable.addClassName("ps-login-note");
        unavailable.getElement().setAttribute("role", "status");

        VerticalLayout card = new VerticalLayout(
                new H2(getTranslation("recovery.title")),
                new Paragraph(getTranslation("recovery.description")),
                email, submit, unavailable,
                new RouterLink(getTranslation("recovery.back"), LoginView.class));
        card.addClassName("ps-card-surface");
        card.setMaxWidth("26rem");
        add(card);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("page.recovery");
    }
}
