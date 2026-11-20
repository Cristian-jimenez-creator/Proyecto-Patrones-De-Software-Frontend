package com.poolguard.web.ui;

import com.poolguard.web.api.CameraFeedClient;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Overview | PoolGuard")
@PermitAll
public final class DashboardView extends VerticalLayout {
    public DashboardView(CameraFeedClient cameraFeedClient) {
        addClassName("view");
        addClassName("dashboard-view");
        setSpacing(false);
        setPadding(false);

        CameraFeedClient.FrameResult camera = cameraFeedClient.latestFrame();
        boolean connected = camera.jpeg() != null && "Live camera frame".equals(camera.status());

        Div heading = new Div();
        heading.addClassName("page-heading");
        Paragraph eyebrow = new Paragraph("MONITORING OVERVIEW");
        eyebrow.addClassName("eyebrow");
        H1 title = new H1("A clearer view of pool safety.");
        title.addClassName("page-title");
        Paragraph subtitle = new Paragraph("Your camera and verified safety signals, together in one calm workspace.");
        subtitle.addClassName("page-subtitle");
        heading.add(eyebrow, title, subtitle);

        Div hero = new Div();
        hero.addClassName("welcome-hero");
        Div heroCopy = new Div();
        heroCopy.addClassName("hero-copy");
        Span heroTag = new Span("POOLGUARD  /  LIVE MONITORING");
        heroTag.addClassName("hero-tag");
        H2 heroTitle = new H2("Every second matters.");
        Paragraph heroText = new Paragraph("Keep the camera in view and the real signals easy to understand. PoolGuard shows information received from your connected system.");
        heroText.addClassName("hero-description");
        Anchor cameraLink = new Anchor("/cameras", "Open live camera");
        cameraLink.addClassName("primary-link");
        Anchor setupLink = new Anchor("/cameras/add", "Camera setup guide");
        setupLink.addClassName("hero-secondary-link");
        Div heroActions = new Div(cameraLink, setupLink);
        heroActions.addClassName("hero-actions");
        heroCopy.add(heroTag, heroTitle, heroText, heroActions);

        Div orbit = new Div();
        orbit.addClassName("hero-orbit");
        Div orbitInner = new Div();
        orbitInner.addClassName("hero-orbit-inner");
        Div orbitCore = new Div(new Span("PG"));
        orbitCore.addClassName("hero-orbit-core");
        Div orbitDot = new Div();
        orbitDot.addClassName("hero-orbit-dot");
        Div orbitArt = new Div(orbit, orbitInner, orbitCore, orbitDot);
        orbitArt.addClassName("hero-art");
        hero.add(heroCopy, orbitArt);

        Div sectionHeader = new Div();
        sectionHeader.addClassName("section-heading");
        H2 sectionTitle = new H2("System snapshot");
        Paragraph sectionHint = new Paragraph("Live connection state");
        sectionHeader.add(sectionTitle, sectionHint);

        Div cameraCard = snapshotCard("01", "CAMERA FEED",
                connected ? "Camera connected" : "Waiting for camera",
                connected ? "The protected live feed is available." : camera.status(),
                connected ? "state-live" : "state-waiting");
        Div analysisCard = snapshotCard("02", "SAFETY ANALYSIS",
                "Verified signals only",
                "Risk information appears when a configured analyzer returns real results.",
                "state-neutral");
        Div privacyCard = snapshotCard("03", "DATA HANDLING",
                "Private camera path",
                "The phone stream is relayed through the protected API; camera frames stay in memory.",
                "state-neutral");
        Div cards = new Div(cameraCard, analysisCard, privacyCard);
        cards.addClassName("snapshot-grid");

        Div note = new Div();
        note.addClassName("dashboard-note");
        Span noteMark = new Span("i");
        noteMark.addClassName("note-mark");
        Div noteCopy = new Div(new H2("Built around real signals"),
                new Paragraph("PoolGuard does not invent detections. If a camera or analyzer is offline, its status is shown clearly here."));
        note.add(noteMark, noteCopy);

        add(heading, hero, sectionHeader, cards, note);
    }

    private Div snapshotCard(String number, String label, String value, String detail, String stateClass) {
        Div card = new Div();
        card.addClassName("snapshot-card");
        Span index = new Span(number);
        index.addClassName("snapshot-index");
        Paragraph eyebrow = new Paragraph(label);
        eyebrow.addClassName("snapshot-label");
        Span indicator = new Span();
        indicator.addClassNames("status-dot", stateClass);
        H2 title = new H2(value);
        title.addClassName("snapshot-value");
        Paragraph description = new Paragraph(detail);
        description.addClassName("snapshot-description");
        Div top = new Div(index, eyebrow, indicator);
        top.addClassName("snapshot-card-top");
        card.add(top, title, description);
        return card;
    }
}
