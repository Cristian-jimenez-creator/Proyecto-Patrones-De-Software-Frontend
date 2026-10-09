package com.smartpool.backend.config;

import com.smartpool.backend.model.AppUser;
import com.smartpool.backend.model.Camera;
import com.smartpool.backend.model.Pool;
import com.smartpool.backend.model.Types.AlertStatus;
import com.smartpool.backend.model.Types.CameraStatus;
import com.smartpool.backend.model.Types.PoolStatus;
import com.smartpool.backend.model.Types.Risk;
import com.smartpool.backend.model.Types.Role;
import com.smartpool.backend.repository.CameraRepository;
import com.smartpool.backend.repository.PoolRepository;
import com.smartpool.backend.repository.UserRepository;
import com.smartpool.backend.service.AlertService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Fills the empty database with the same demo data used by the frontend. */
@Component
public class DataSeeder implements CommandLineRunner {
    private final PoolRepository pools;
    private final CameraRepository cameras;
    private final UserRepository users;
    private final AlertService alerts;

    public DataSeeder(PoolRepository pools, CameraRepository cameras, UserRepository users, AlertService alerts) {
        this.pools = pools;
        this.cameras = cameras;
        this.users = users;
        this.alerts = alerts;
    }

    @Override
    public void run(String... args) {
        // Keep a persistent cloud database intact across restarts and deployments.
        if (pools.count() > 0) return;

        pools.saveAll(List.of(
                new Pool("Blue Wave Pool", "North Wing · Level 1", 3, PoolStatus.NORMAL, 14, Risk.LOW),
                new Pool("Aqua Club", "Fitness Center", 2, PoolStatus.OBSERVATION, 9, Risk.MEDIUM),
                new Pool("Sunset Lagoon", "Resort Garden", 2, PoolStatus.ALERT, 21, Risk.HIGH),
                new Pool("Kids Splash", "Family Area", 2, PoolStatus.NORMAL, 17, Risk.LOW),
                new Pool("Olympic Arena", "University Campus", 2, PoolStatus.NORMAL, 6, Risk.LOW),
                new Pool("Hotel Terrace", "Rooftop · Level 12", 1, PoolStatus.EMERGENCY, 5, Risk.CRITICAL)));

        cameras.saveAll(List.of(
                cam(1, "Blue Wave Pool", "Shallow end", CameraStatus.ONLINE, "1080p"),
                cam(2, "Blue Wave Pool", "Deep end", CameraStatus.ONLINE, "1080p"),
                cam(3, "Blue Wave Pool", "Entrance", CameraStatus.OFFLINE, "720p"),
                cam(4, "Aqua Club", "Lane area", CameraStatus.ONLINE, "1080p"),
                cam(5, "Aqua Club", "Pool deck", CameraStatus.MAINTENANCE, "720p"),
                cam(6, "Sunset Lagoon", "East side", CameraStatus.ONLINE, "4K"),
                cam(7, "Sunset Lagoon", "West side", CameraStatus.ONLINE, "1080p"),
                cam(8, "Kids Splash", "Wading area", CameraStatus.ONLINE, "1080p"),
                cam(9, "Kids Splash", "Slides", CameraStatus.ERROR, "1080p"),
                cam(10, "Olympic Arena", "Main pool", CameraStatus.ONLINE, "4K"),
                cam(11, "Olympic Arena", "Diving area", CameraStatus.ONLINE, "1080p"),
                cam(12, "Hotel Terrace", "Rooftop", CameraStatus.ONLINE, "1080p")));

        users.saveAll(List.of(
                new AppUser("Laura Mendez", "laura.mendez@smartpool.demo", Role.ADMINISTRATOR, true),
                new AppUser("Carlos Rojas", "carlos.rojas@smartpool.demo", Role.SUPERVISOR, true),
                new AppUser("Ana Torres", "ana.torres@smartpool.demo", Role.OPERATOR, true),
                new AppUser("David Silva", "david.silva@smartpool.demo", Role.OPERATOR, true),
                new AppUser("Marta Ruiz", "marta.ruiz@smartpool.demo", Role.SUPERVISOR, false),
                new AppUser("Pedro Gil", "pedro.gil@smartpool.demo", Role.OPERATOR, false)));

        // Oldest first so ids grow with time.
        alerts.create("Hotel Terrace", "CAM-012", Risk.CRITICAL, "Possible drowning pattern detected", AlertStatus.CLOSED, at(4, "18:55:44"));
        alerts.create("Sunset Lagoon", "CAM-007", Risk.LOW, "Camera view partially obstructed", AlertStatus.CLOSED, at(3, "11:03:09"));
        alerts.create("Olympic Arena", "CAM-010", Risk.HIGH, "Prolonged submersion detected", AlertStatus.CLOSED, at(2, "15:36:27"));
        alerts.create("Blue Wave Pool", "CAM-002", Risk.LOW, "Crowding detected in shallow end", AlertStatus.CLOSED, at(1, "12:10:51"));
        alerts.create("Kids Splash", "CAM-008", Risk.MEDIUM, "Child detected alone near pool edge", AlertStatus.ATTENDED, at(1, "17:22:05"));
        alerts.create("Aqua Club", "CAM-004", Risk.MEDIUM, "Swimmer outside normal lane pattern", AlertStatus.NEW, at(0, "08:47:10"));
        alerts.create("Sunset Lagoon", "CAM-006", Risk.HIGH, "Irregular movement detected in deep area", AlertStatus.NEW, at(0, "09:05:33"));
        alerts.create("Hotel Terrace", "CAM-012", Risk.CRITICAL, "Swimmer motionless underwater for several seconds", AlertStatus.CONFIRMED, at(0, "09:41:12"));
    }

    private static Camera cam(int n, String pool, String location, CameraStatus status, String resolution) {
        boolean online = status == CameraStatus.ONLINE;
        return new Camera(String.format("CAM-%03d", n), pool, location, status,
                online ? "AI Active" : "Paused", resolution, online ? "Just now" : "12 min ago");
    }

    private static LocalDateTime at(int daysAgo, String time) {
        return LocalDateTime.of(LocalDate.now().minusDays(daysAgo), LocalTime.parse(time));
    }
}
