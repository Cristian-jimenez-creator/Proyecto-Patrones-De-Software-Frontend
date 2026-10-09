package com.smartpool.backend.repository;

import com.smartpool.backend.model.SecurityAlert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<SecurityAlert, String> {
    List<SecurityAlert> findAllByOrderByDateDescTimeDesc();
}
