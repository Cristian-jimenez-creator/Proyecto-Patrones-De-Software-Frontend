package com.smartpool.backend.controller;

import com.smartpool.backend.model.AiSnapshot;
import com.smartpool.backend.model.Camera;
import com.smartpool.backend.model.Types.CameraStatus;
import com.smartpool.backend.repository.CameraRepository;
import com.smartpool.backend.service.AiAnalyzer;
import com.smartpool.backend.service.NotFoundException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {
    private final CameraRepository cameras;
    private final AiAnalyzer analyzer;

    public CameraController(CameraRepository cameras, AiAnalyzer analyzer) {
        this.cameras = cameras;
        this.analyzer = analyzer;
    }

    @GetMapping
    public List<Camera> list(@RequestParam(required = false) String pool) {
        return pool == null ? cameras.findAll() : cameras.findByPool(pool);
    }

    @GetMapping("/{id}")
    public Camera get(@PathVariable String id) {
        return cameras.findById(id).orElseThrow(() -> new NotFoundException("Camera " + id + " not found"));
    }

    @GetMapping("/{id}/analysis")
    public AiSnapshot analysis(@PathVariable String id) {
        return analyzer.analyze(get(id));
    }

    @PatchMapping("/{id}/status")
    public Camera changeStatus(@PathVariable String id, @RequestParam CameraStatus value) {
        Camera camera = get(id);
        camera.status = value;
        camera.monitoring = value == CameraStatus.ONLINE ? "AI Active" : "Paused";
        camera.lastUpdate = "Just now";
        return cameras.save(camera);
    }
}
