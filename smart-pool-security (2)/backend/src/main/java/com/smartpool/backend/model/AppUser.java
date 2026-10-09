package com.smartpool.backend.model;

import com.smartpool.backend.model.Types.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @NotBlank
    public String name;
    @NotBlank
    @Email
    public String email;
    @NotNull
    @Enumerated(EnumType.STRING)
    public Role role;
    public boolean active = true;

    public AppUser() {}

    public AppUser(String name, String email, Role role, boolean active) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.active = active;
    }
}
