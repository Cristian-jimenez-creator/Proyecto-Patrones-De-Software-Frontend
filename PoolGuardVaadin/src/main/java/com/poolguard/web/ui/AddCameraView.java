package com.poolguard.web.ui;

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

@Route(value = "cameras/add", layout = MainLayout.class)
@PageTitle("Connect camera | PoolGuard")
@PermitAll
public final class AddCameraView extends VerticalLayout {
    public AddCameraView() {
        addClassName("view");
        addClassName("setup-view");
        setSpacing(false);
        setPadding(false);

        Paragraph eyebrow = new Paragraph("QUICK SETUP");
        eyebrow.addClassName("eyebrow");
        H1 title = new H1("Bring your camera online.");
        title.addClassName("page-title");
        Paragraph subtitle = new Paragraph("Connect the phone and local client once. PoolGuard will keep the live view ready for your team.");
        subtitle.addClassName("page-subtitle");
        Div heading = new Div(eyebrow, title, subtitle);
        heading.addClassName("page-heading");

        Div steps = new Div(
                step("01", "Start IP Webcam", "Open IP Webcam on your phone, start its server and keep the phone on the camera Wi-Fi network."),
                step("02", "Run the local client", "On a computer connected to the same Wi-Fi, run the PoolGuard camera client with the video URL shown by IP Webcam."),
                step("03", "Open live camera", "When PoolGuard receives the first real frame, the camera page switches to a continuous live stream."));
        steps.addClassName("setup-steps");

        Div security = new Div(new Span("◇"), new Div(
                new H2("Keep the camera link private"),
                new Paragraph("Use the current address shown by IP Webcam only on the local computer. Do not paste a private phone address or the ingest token into the public web page or GitHub.")));
        security.addClassName("setup-security");

        Anchor cameraLink = new Anchor("/cameras", "Go to live camera");
        cameraLink.addClassName("primary-link");
        Div footer = new Div(cameraLink);
        footer.addClassName("setup-footer");
        add(heading, steps, security, footer);
    }

    private Div step(String number, String title, String description) {
        Span marker = new Span(number);
        marker.addClassName("step-number");
        H2 heading = new H2(title);
        Paragraph text = new Paragraph(description);
        text.addClassName("step-description");
        Div content = new Div(heading, text);
        content.addClassName("step-content");
        Div card = new Div(marker, content);
        card.addClassName("setup-step");
        return card;
    }
}
