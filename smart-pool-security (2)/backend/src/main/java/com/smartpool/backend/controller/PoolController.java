package com.smartpool.backend.controller;

import com.smartpool.backend.model.Pool;
import com.smartpool.backend.repository.PoolRepository;
import com.smartpool.backend.service.NotFoundException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pools")
public class PoolController {
    private final PoolRepository pools;

    public PoolController(PoolRepository pools) {
        this.pools = pools;
    }

    @GetMapping
    public List<Pool> list() {
        return pools.findAll();
    }

    @GetMapping("/{id}")
    public Pool get(@PathVariable Long id) {
        return pools.findById(id).orElseThrow(() -> new NotFoundException("Pool " + id + " not found"));
    }
}
