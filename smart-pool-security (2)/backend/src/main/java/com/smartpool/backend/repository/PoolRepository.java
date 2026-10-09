package com.smartpool.backend.repository;

import com.smartpool.backend.model.Pool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoolRepository extends JpaRepository<Pool, Long> {}
