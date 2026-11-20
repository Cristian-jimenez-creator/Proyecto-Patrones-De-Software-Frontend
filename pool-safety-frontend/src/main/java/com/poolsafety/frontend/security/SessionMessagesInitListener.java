package com.poolsafety.frontend.security;

import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import org.springframework.stereotype.Component;

/** Redirects safely to the login view when the session expires, without a technical dialog. */
@Component
public class SessionMessagesInitListener implements VaadinServiceInitListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().setSystemMessagesProvider(info -> {
            CustomizedSystemMessages messages = new CustomizedSystemMessages();
            messages.setSessionExpiredURL("/login?expired");
            messages.setSessionExpiredCaption(null);
            messages.setSessionExpiredMessage(null);
            return messages;
        });
    }
}
