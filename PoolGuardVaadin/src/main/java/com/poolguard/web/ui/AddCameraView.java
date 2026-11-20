package com.poolguard.web.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "cameras/add", layout = MainLayout.class)
@PageTitle("Add camera | PoolGuard")
public final class AddCameraView extends VerticalLayout {
    public AddCameraView() {
        addClassName("view");
        add(new H1("Connect the phone camera"),
                new Paragraph("Start IP Webcam on the phone, then run the PoolGuard camera client on a computer connected to the same Wi-Fi."),
                new Paragraph("Set POOLGUARD_CAMERA_URL on that computer to the stream URL shown by IP Webcam, for example http://192.168.101.11:8080/video."),
                new Paragraph("Do not enter the phone's private 192.168.x.x address in Render. The local client forwards frames to Render over HTTPS."));
    }
}
