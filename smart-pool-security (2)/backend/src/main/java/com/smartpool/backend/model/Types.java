package com.smartpool.backend.model;

/** Enums shared by the whole API. Names match the JavaFX frontend. */
public final class Types {
    private Types() {}

    public enum Risk { LOW, MEDIUM, HIGH, CRITICAL }
    public enum PoolStatus { NORMAL, OBSERVATION, ALERT, EMERGENCY }
    public enum CameraStatus { ONLINE, OFFLINE, MAINTENANCE, ERROR }
    public enum AlertStatus { NEW, CONFIRMED, ATTENDED, CLOSED }
    public enum Role { ADMINISTRATOR, SUPERVISOR, OPERATOR }
}
