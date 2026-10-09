package com.smartpool.backend.model;

import com.smartpool.backend.model.Types.CameraStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class Camera {
    @Id
    public String id;
    public String pool;
    public String location;
    @Enumerated(EnumType.STRING)
    public CameraStatus status;
    public String monitoring;
    public String resolution;
    public String lastUpdate;

    public Camera() {}

    public Camera(String id, String pool, String location, CameraStatus status,
                  String monitoring, String resolution, String lastUpdate) {
        this.id = id;
        this.pool = pool;
        this.location = location;
        this.status = status;
        this.monitoring = monitoring;
        this.resolution = resolution;
        this.lastUpdate = lastUpdate;
    }
}
