package com.smartpool.backend.service;

import com.smartpool.backend.model.AiSnapshot;
import com.smartpool.backend.model.Camera;

/** Strategy: any AI engine can implement this (simulated today, real model later). */
public interface AiAnalyzer {
    AiSnapshot analyze(Camera camera);
}
