package com.poolguard.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

@SpringBootApplication
@Theme("poolguard")
public class PoolGuardWebApplication implements AppShellConfigurator {
    public static void main(String[] args) { SpringApplication.run(PoolGuardWebApplication.class, args); }
}
