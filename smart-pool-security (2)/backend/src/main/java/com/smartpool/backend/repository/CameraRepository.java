package com.smartpool.backend.repository;

import com.smartpool.backend.model.Camera;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CameraRepository extends JpaRepository<Camera, String> {
    List<Camera> findByPool(String pool);
}
