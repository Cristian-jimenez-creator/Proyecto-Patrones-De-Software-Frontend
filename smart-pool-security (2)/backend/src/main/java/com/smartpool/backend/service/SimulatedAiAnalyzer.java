package com.smartpool.backend.service;

import com.smartpool.backend.model.AiSnapshot;
import com.smartpool.backend.model.Camera;
import com.smartpool.backend.model.Types.CameraStatus;
import com.smartpool.backend.model.Types.Risk;
import org.springframework.stereotype.Component;

/** Fake analysis: values depend on the camera id, so they are stable between calls. */
@Component
public class SimulatedAiAnalyzer implements AiAnalyzer {
    @Override
    public AiSnapshot analyze(Camera camera) {
        if (camera.status != CameraStatus.ONLINE) {
            return new AiSnapshot(0, Risk.LOW, 0, "No data", "Offline", true);
        }
        int h = Math.abs(camera.id.hashCode());
        int level = h % 3;
        String[] movement = { "Normal swimming", "Active play", "Irregular movement" };
        return new AiSnapshot(1 + h % 6, Risk.values()[level], 72 + h % 26, movement[level], "Monitoring", true);
    }
}
