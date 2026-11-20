package com.poolguard.web.ui;

import com.poolguard.web.api.CameraFeedClient;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "cameras", layout = MainLayout.class)
@PageTitle("Cameras | PoolGuard")
public final class CamerasView extends VerticalLayout {
    private final CameraFeedClient cameraFeedClient;
    private final Paragraph status = new Paragraph("Checking camera feed…");
    private final Image frame = new Image("Latest camera frame", "Latest frame sent from the phone camera");
    private Registration pollRegistration;

    public CamerasView(CameraFeedClient cameraFeedClient) {
        this.cameraFeedClient = cameraFeedClient;
        addClassName("view");
        frame.setMaxWidth("100%");
        frame.setMaxHeight("70vh");
        frame.getStyle().set("object-fit", "contain");
        frame.setVisible(false);
        add(new H1("IP camera"), status,
                new Paragraph("The local Java client reads the phone over Wi-Fi and sends the newest frame to the protected Render API."),
                frame);
    }

    @Override
    protected void onAttach(AttachEvent event) {
        UI ui = event.getUI();
        ui.setPollInterval(1_500);
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
        CameraFeedClient.FrameResult latest = cameraFeedClient.latestFrame();
        status.setText(latest.status());
        if (latest.jpeg() == null) {
            frame.setVisible(false);
            return;
        }
        frame.setSrc(latest.dataUri());
        frame.setVisible(true);
    }
}
