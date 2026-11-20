package com.poolguard.web.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "cameras/add", layout = MainLayout.class)
@PageTitle("Add camera | PoolGuard")
public final class AddCameraView extends VerticalLayout {
    public AddCameraView() {
        addClassName("view"); TextField name = new TextField("Camera name"); TextField address = new TextField("MJPEG stream URL"); address.setPlaceholder("http://192.168.101.11:8080/video");
        Button test = new Button("Test connection"); test.addClickListener(event -> { address.setInvalid(address.isEmpty()); address.setErrorMessage("Enter the real camera stream URL."); });
        add(new H1("Add IP camera"), name, address, test);
    }
}
