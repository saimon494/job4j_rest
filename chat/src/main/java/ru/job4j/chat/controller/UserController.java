package ru.job4j.chat.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.repository.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/users")
public class UserController {

    private PersonRepository users;
    private BCryptPasswordEncoder encoder;

    public UserController(PersonRepository users,
                          BCryptPasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person user) {
        if (user.getLogin().isEmpty() || user.getPassword().isEmpty()) {
            throw new NullPointerException("Empty login or password");
        }
        var person = Person.of(user.getId(), user.getLogin(),
                encoder.encode(user.getPassword()));
        users.save(person);
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return StreamSupport.stream(
                        users.findAll().spliterator(),
                        false)
                .collect(Collectors.toList());
    }
}
