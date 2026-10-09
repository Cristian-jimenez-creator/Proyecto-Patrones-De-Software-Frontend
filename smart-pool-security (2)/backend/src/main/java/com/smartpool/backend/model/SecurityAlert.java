package com.smartpool.backend.model;

import com.smartpool.backend.model.Types.AlertStatus;
import com.smartpool.backend.model.Types.Risk;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/** An alert. When it is closed it also works as an incident in the history. */
@Entity
public class SecurityAlert {
    @Id
    public String id;
    @Column(name = "alert_date")
    public String date;
    @Column(name = "alert_time")
    public String time;
    public String pool;
    public String camera;
    @Enumerated(EnumType.STRING)
    public Risk risk;
    public String description;
    @Enumerated(EnumType.STRING)
    public AlertStatus status;
    @ElementCollection(fetch = FetchType.EAGER)
    public List<String> observations = new ArrayList<>();

    public SecurityAlert() {}

    public SecurityAlert(String id, String date, String time, String pool, String camera,
                         Risk risk, String description, AlertStatus status) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.pool = pool;
        this.camera = camera;
        this.risk = risk;
        this.description = description;
        this.status = status;
    }
}
