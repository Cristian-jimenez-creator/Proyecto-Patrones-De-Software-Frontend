package com.poolsafety.frontend;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

// ASSUMPTION: @Theme still works in Vaadin 25; if it does not, replace it with @StyleSheet entries.
@Theme("poolsafety")
@JsModule("./src/animations/motion.ts")
public class AppShell implements AppShellConfigurator {
}
