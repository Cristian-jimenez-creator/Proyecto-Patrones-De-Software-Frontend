package com.smartpool.backend.model;

import com.smartpool.backend.model.Types.Risk;

/** Result of analyzing one camera. "simulated" is true until a real AI model is connected. */
public record AiSnapshot(int people, Risk risk, int confidence, String movement, String detection, boolean simulated) {}
