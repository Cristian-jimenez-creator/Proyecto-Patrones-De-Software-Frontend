package com.smartpool.backend.controller;

import com.smartpool.backend.model.SecurityAlert;
import com.smartpool.backend.model.Types.AlertStatus;
import com.smartpool.backend.model.Types.Risk;
import com.smartpool.backend.service.AlertService;
import com.smartpool.backend.service.AlertStreamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/** Alerts and incident history (the history is this same list with filters). */
@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    public record ObservationRequest(@NotBlank String text) {}

    private final AlertService service;
    private final AlertStreamService stream;

    public AlertController(AlertService service, AlertStreamService stream) {
        this.service = service;
        this.stream = stream;
    }

    @GetMapping
    public List<SecurityAlert> list(@RequestParam(required = false) String pool,
                                    @RequestParam(required = false) String camera,
                                    @RequestParam(required = false) Risk risk,
                                    @RequestParam(required = false) AlertStatus status,
                                    @RequestParam(required = false) String date) {
        return service.find(pool, camera, risk, status, date);
    }

    @GetMapping("/{id}")
    public SecurityAlert get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping("/{id}/confirm")
    public SecurityAlert confirm(@PathVariable String id) {
        return service.confirm(id);
    }

    @PostMapping("/{id}/attend")
    public SecurityAlert attend(@PathVariable String id) {
        return service.attend(id);
    }

    @PostMapping("/{id}/close")
    public SecurityAlert close(@PathVariable String id) {
        return service.close(id);
    }

    @PostMapping("/{id}/observations")
    public SecurityAlert observe(@PathVariable String id, @Valid @RequestBody ObservationRequest body) {
        return service.addObservation(id, body.text());
    }

    @PostMapping("/simulate-critical")
    @ResponseStatus(HttpStatus.CREATED)
    public SecurityAlert simulateCritical() {
        return service.simulateCritical();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return stream.subscribe();
    }
}
