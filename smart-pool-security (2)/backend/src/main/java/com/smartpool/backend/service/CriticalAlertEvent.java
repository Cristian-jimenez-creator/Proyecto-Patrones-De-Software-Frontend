package com.smartpool.backend.service;

import com.smartpool.backend.model.SecurityAlert;

/** Event published when a new critical alert is created (Observer pattern). */
public record CriticalAlertEvent(SecurityAlert alert) {}
