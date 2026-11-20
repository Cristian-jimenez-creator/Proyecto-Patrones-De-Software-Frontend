package com.poolguard.web.api;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public final class ApiErrorHandler {
    public void show(int status) {
        Notification notification = Notification.show(ApiErrorMessageMapper.messageFor(status), 5000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
