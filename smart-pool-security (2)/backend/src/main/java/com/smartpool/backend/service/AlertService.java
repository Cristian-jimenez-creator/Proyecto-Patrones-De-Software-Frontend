package com.smartpool.backend.service;

import com.smartpool.backend.model.Pool;
import com.smartpool.backend.model.SecurityAlert;
import com.smartpool.backend.model.Types.AlertStatus;
import com.smartpool.backend.model.Types.CameraStatus;
import com.smartpool.backend.model.Types.Risk;
import com.smartpool.backend.repository.AlertRepository;
import com.smartpool.backend.repository.CameraRepository;
import com.smartpool.backend.repository.PoolRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/** Business rules for alerts and incidents. */
@Service
public class AlertService {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final AlertRepository alerts;
    private final PoolRepository pools;
    private final CameraRepository cameras;
    private final ApplicationEventPublisher events;

    public AlertService(AlertRepository alerts, PoolRepository pools, CameraRepository cameras,
                        ApplicationEventPublisher events) {
        this.alerts = alerts;
        this.pools = pools;
        this.cameras = cameras;
        this.events = events;
    }

    /** Newest first. Every filter is optional (null = ignore). */
    public List<SecurityAlert> find(String pool, String camera, Risk risk, AlertStatus status, String date) {
        return alerts.findAllByOrderByDateDescTimeDesc().stream()
                .filter(a -> pool == null || a.pool.equals(pool))
                .filter(a -> camera == null || a.camera.equals(camera))
                .filter(a -> risk == null || a.risk == risk)
                .filter(a -> status == null || a.status == status)
                .filter(a -> date == null || a.date.equals(date))
                .toList();
    }

    public SecurityAlert get(String id) {
        return alerts.findById(id).orElseThrow(() -> new NotFoundException("Alert " + id + " not found"));
    }

    public synchronized SecurityAlert create(String pool, String camera, Risk risk, String description,
                                             AlertStatus status, LocalDateTime when) {
        String id = "AL-" + (1001 + alerts.count());
        SecurityAlert saved = alerts.save(new SecurityAlert(id, when.toLocalDate().toString(),
                when.format(TIME), pool, camera, risk, description, status));
        if (risk == Risk.CRITICAL && status == AlertStatus.NEW) {
            events.publishEvent(new CriticalAlertEvent(saved));
        }
        return saved;
    }

    /** Same rules as the frontend buttons. */
    public SecurityAlert confirm(String id) {
        return move(id, AlertStatus.CONFIRMED, AlertStatus.NEW);
    }

    public SecurityAlert attend(String id) {
        return move(id, AlertStatus.ATTENDED, AlertStatus.NEW, AlertStatus.CONFIRMED);
    }

    public SecurityAlert close(String id) {
        return move(id, AlertStatus.CLOSED, AlertStatus.NEW, AlertStatus.CONFIRMED, AlertStatus.ATTENDED);
    }

    private SecurityAlert move(String id, AlertStatus target, AlertStatus... allowedFrom) {
        SecurityAlert alert = get(id);
        if (!Arrays.asList(allowedFrom).contains(alert.status)) {
            throw new IllegalStateException("Alert " + id + " cannot change from " + alert.status + " to " + target);
        }
        alert.status = target;
        return alerts.save(alert);
    }

    public SecurityAlert addObservation(String id, String text) {
        SecurityAlert alert = get(id);
        alert.observations.add(text);
        return alerts.save(alert);
    }

    /** Creates a SIMULATED critical alert in a random pool. Not a real detection. */
    public SecurityAlert simulateCritical() {
        List<Pool> all = pools.findAll();
        Pool pool = all.get(new Random().nextInt(all.size()));
        String camera = cameras.findByPool(pool.name).stream()
                .filter(c -> c.status == CameraStatus.ONLINE)
                .map(c -> c.id).findFirst().orElse("CAM-001");
        return create(pool.name, camera, Risk.CRITICAL, "Possible drowning detected (simulated)",
                AlertStatus.NEW, LocalDateTime.now());
    }
}
