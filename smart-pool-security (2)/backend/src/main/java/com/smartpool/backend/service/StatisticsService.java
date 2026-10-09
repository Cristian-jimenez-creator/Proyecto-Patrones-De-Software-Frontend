package com.smartpool.backend.service;

import com.smartpool.backend.model.Camera;
import com.smartpool.backend.model.Pool;
import com.smartpool.backend.model.SecurityAlert;
import com.smartpool.backend.model.Types.AlertStatus;
import com.smartpool.backend.model.Types.CameraStatus;
import com.smartpool.backend.model.Types.PoolStatus;
import com.smartpool.backend.model.Types.Risk;
import com.smartpool.backend.repository.AlertRepository;
import com.smartpool.backend.repository.CameraRepository;
import com.smartpool.backend.repository.PoolRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
    public record Statistics(int totalDetections, long totalAlerts, long criticalAlerts, long resolvedIncidents,
                             long activeCameras, long totalCameras, Map<PoolStatus, Long> poolsByStatus,
                             Map<Risk, Long> alertsByRisk, List<Integer> weeklyDetections) {}

    private final AlertRepository alerts;
    private final CameraRepository cameras;
    private final PoolRepository pools;

    public StatisticsService(AlertRepository alerts, CameraRepository cameras, PoolRepository pools) {
        this.alerts = alerts;
        this.cameras = cameras;
        this.pools = pools;
    }

    public Statistics compute() {
        List<SecurityAlert> allAlerts = alerts.findAll();
        List<Camera> allCameras = cameras.findAll();
        List<Pool> allPools = pools.findAll();

        Map<PoolStatus, Long> byStatus = new EnumMap<>(PoolStatus.class);
        for (PoolStatus s : PoolStatus.values()) {
            byStatus.put(s, allPools.stream().filter(p -> p.status == s).count());
        }
        Map<Risk, Long> byRisk = new EnumMap<>(Risk.class);
        for (Risk r : Risk.values()) {
            byRisk.put(r, allAlerts.stream().filter(a -> a.risk == r).count());
        }
        // Demo values: replace with a real "detections" table when the AI is connected.
        List<Integer> weekly = List.of(42, 57, 38, 64, 71, 55, 48);

        return new Statistics(weekly.stream().mapToInt(Integer::intValue).sum(), allAlerts.size(),
                byRisk.get(Risk.CRITICAL), allAlerts.stream().filter(a -> a.status == AlertStatus.CLOSED).count(),
                allCameras.stream().filter(c -> c.status == CameraStatus.ONLINE).count(), allCameras.size(),
                byStatus, byRisk, weekly);
    }
}
