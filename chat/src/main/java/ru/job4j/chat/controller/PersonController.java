package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.Operation;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.handlers.IllegalPasswordException;
import ru.job4j.chat.repository.PersonRepository;
import ru.job4j.chat.service.PatchService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            PersonController.class.getSimpleName());

    private final PersonRepository persons;

    private final ObjectMapper objectMapper;

    public PersonController(final PersonRepository persons, ObjectMapper objectMapper) {
        this.persons = persons;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return StreamSupport.stream(this.persons.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Person not found");
        }
        return new ResponseEntity<>(person.orElse(new Person()), HttpStatus.OK);
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Person> create(@Valid @RequestBody Person person)
            throws IllegalPasswordException {
        if (person.getLogin().isEmpty() || person.getPassword().isEmpty()) {
            throw new NullPointerException("Empty login or password");
        }
        if (person.getPassword().length() < 8) {
            throw new IllegalPasswordException(
                    "Use at least 8 symbols in password");
        }
        return new ResponseEntity<>(
                this.persons.save(person), HttpStatus.CREATED);
    }

    @PutMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Empty login or password");
        }
        this.persons.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public ResponseEntity<Void> delete(@Valid @PathVariable int id) {
        if (id == 0) {
            throw new NullPointerException("Empty id");
        }
        var person = new Person();
        person.setId(id);
        this.persons.delete(person);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = {IllegalPasswordException.class })
    public void exceptionHandler(Exception e, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }

    @PatchMapping("/")
    public Person patch(@RequestBody Person person)
            throws InvocationTargetException, IllegalAccessException {
        var newPerson = persons.findById(person.getId());
        if (newPerson.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var patchService = new PatchService<Person>();
        persons.save(patchService.getPatch(newPerson.get(), person));
        return newPerson.get();
    }
}
