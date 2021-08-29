package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.Operation;
import ru.job4j.chat.domain.Role;
import ru.job4j.chat.repository.RoleRepository;
import ru.job4j.chat.service.PatchService;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roles;

    public RoleController(final RoleRepository roles) {
        this.roles = roles;
    }

    @GetMapping("/")
    public List<Role> findAll() {
        return StreamSupport.stream(this.roles.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> findById(@PathVariable int id) {
        var role = this.roles.findById(id);
        if (role.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Role not found");
        }
        return new ResponseEntity<>(role.orElse(new Role()), HttpStatus.OK);
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) {
        if (role.getName().isEmpty()) {
            throw new NullPointerException("Empty role");
        }
        return new ResponseEntity<>(
                this.roles.save(role), HttpStatus.CREATED);
    }

    @PutMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Role role) {
        if (role.getName().isEmpty()) {
            throw new NullPointerException("Empty role");
        }
        this.roles.save(role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public ResponseEntity<Void> delete(@Valid @PathVariable int id) {
        if (id == 0) {
            throw new NullPointerException("Empty id");
        }
        var role = new Role();
        role.setId(id);
        this.roles.delete(role);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/")
    public Role patch(@RequestBody Role role)
            throws InvocationTargetException, IllegalAccessException {
        var newRole = roles.findById(role.getId());
        if (newRole.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var patchService = new PatchService<Role>();
        roles.save(patchService.getPatch(newRole.get(), role));
        return newRole.get();
    }
}
