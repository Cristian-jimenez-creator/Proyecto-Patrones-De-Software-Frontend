package com.smartpool.backend.controller;

import com.smartpool.backend.model.AppUser;
import com.smartpool.backend.repository.UserRepository;
import com.smartpool.backend.service.NotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    private AppUser find(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }

    @GetMapping
    public List<AppUser> list() {
        return users.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser create(@Valid @RequestBody AppUser body) {
        body.id = null;
        return users.save(body);
    }

    @PutMapping("/{id}")
    public AppUser update(@PathVariable Long id, @Valid @RequestBody AppUser body) {
        AppUser user = find(id);
        user.name = body.name;
        user.email = body.email;
        user.role = body.role;
        return users.save(user);
    }

    @PatchMapping("/{id}/active")
    public AppUser setActive(@PathVariable Long id, @RequestParam boolean value) {
        AppUser user = find(id);
        user.active = value;
        return users.save(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        users.delete(find(id));
    }
}
