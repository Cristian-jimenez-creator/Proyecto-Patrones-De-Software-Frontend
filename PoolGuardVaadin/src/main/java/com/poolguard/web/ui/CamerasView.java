package com.poolguard.web.ui;

import com.poolguard.web.api.CameraFeedClient;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "cameras", layout = MainLayout.class)
@PageTitle("Cameras | PoolGuard")
@PermitAll
public final class CamerasView extends VerticalLayout {
    private final CameraFeedClient cameraFeedClient;
    private final Paragraph status = new Paragraph("Checking camera connection…");
    private final Image frame = new Image("", "Live view from the connected pool camera");
    private final Div waiting = waitingPanel();
    private final Span liveDot = new Span();
    private Registration pollRegistration;
    private boolean streamStarted;

    public CamerasView(CameraFeedClient cameraFeedClient) {
        this.cameraFeedClient = cameraFeedClient;
        addClassName("view");
        addClassName("camera-view");
        setSpacing(false);
        setPadding(false);
        frame.addClassName("camera-frame");
        frame.setVisible(false);

        Paragraph eyebrow = new Paragraph("CAMERA CONTROL");
        eyebrow.addClassName("eyebrow");
        H1 title = new H1("Live camera");
        title.addClassName("page-title");
        Paragraph subtitle = new Paragraph("A continuous view from your connected IP Webcam stream.");
        subtitle.addClassName("page-subtitle");
        Div heading = new Div(eyebrow, title, subtitle);
        heading.addClassName("page-heading");

        liveDot.addClassName("status-dot");
        Span statusLabel = new Span("CAMERA STATUS");
        statusLabel.addClassName("camera-status-label");
        Div statusGroup = new Div(liveDot, statusLabel, status);
        statusGroup.addClassName("camera-status-group");
        Span privacy = new Span("PROTECTED STREAM");
        privacy.addClassName("stream-badge");
        Div toolbar = new Div(statusGroup, privacy);
        toolbar.addClassName("camera-toolbar");

        Div stage = new Div(waiting, frame);
        stage.addClassName("camera-stage");
        Div cameraCard = new Div(toolbar, stage);
        cameraCard.addClassName("camera-card");

        Div footer = new Div(new Span("IP WEBCAM  /  MJPEG"),
                new Paragraph("The live stream is relayed through the protected API. The browser never receives the ingest token."));
        footer.addClassName("camera-footnote");
        add(heading, cameraCard, footer);
    }

    @Override
    protected void onAttach(AttachEvent event) {
        UI ui = event.getUI();
        ui.setPollInterval(2_000);
        pollRegistration = ui.addPollListener(pollEvent -> refreshFrame());
        refreshFrame();
    }

    @Override
    protected void onDetach(DetachEvent event) {
        event.getUI().setPollInterval(-1);
        if (pollRegistration != null) {
            pollRegistration.remove();
            pollRegistration = null;
        }
    }

    private void refreshFrame() {
        if (streamStarted) {
            return;
        }
        CameraFeedClient.FrameResult latest = cameraFeedClient.latestFrame();
        status.setText(latest.status());
        if (latest.jpeg() == null || !"Live camera frame".equals(latest.status())) {
            waiting.setVisible(true);
            frame.setVisible(false);
            return;
        }
        StreamResource liveStream = new StreamResource("camera.mjpg",
                (output, session) -> cameraFeedClient.copyLiveStreamTo(output));
        liveStream.setContentType("multipart/x-mixed-replace; boundary=frame");
        liveStream.setCacheTime(0);
        frame.setSrc(liveStream);
        frame.setVisible(true);
        waiting.setVisible(false);
        status.setText("Live stream from the phone camera.");
        liveDot.addClassName("status-live");
        streamStarted = true;
        getUI().ifPresent(ui -> ui.setPollInterval(-1));
        if (pollRegistration != null) {
            pollRegistration.remove();
            pollRegistration = null;
        }
    }

    private Div waitingPanel() {
        Div previewMark = new Div(new Span("◉"));
        previewMark.addClassName("camera-empty-mark");
        H1 title = new H1("Waiting for camera signal");
        title.addClassName("camera-empty-title");
        Paragraph message = new Paragraph("Start IP Webcam on your phone. PoolGuard will connect automatically when the local camera client is online.");
        message.addClassName("camera-empty-copy");
        Anchor link = new Anchor("/cameras/add", "View connection steps");
        link.addClassName("primary-link");
        Div empty = new Div(previewMark, title, message, link);
        empty.addClassName("camera-empty-state");
        return empty;
    }
}
