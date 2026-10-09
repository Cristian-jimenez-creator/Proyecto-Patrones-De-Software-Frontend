package com.smartpool.backend.model;

import com.smartpool.backend.model.Types.PoolStatus;
import com.smartpool.backend.model.Types.Risk;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/** Entities use public fields to keep the code short (Jackson and Hibernate both support it). */
@Entity
public class Pool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String location;
    public int cameras;
    public int people;
    @Enumerated(EnumType.STRING)
    public PoolStatus status;
    @Enumerated(EnumType.STRING)
    public Risk risk;

    public Pool() {}

    public Pool(String name, String location, int cameras, PoolStatus status, int people, Risk risk) {
        this.name = name;
        this.location = location;
        this.cameras = cameras;
        this.status = status;
        this.people = people;
        this.risk = risk;
    }
}
