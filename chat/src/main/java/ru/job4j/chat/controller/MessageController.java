package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.Operation;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.repository.MessageRepository;
import ru.job4j.chat.service.PatchService;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messages;

    public MessageController(final MessageRepository messages) {
        this.messages = messages;
    }

    @GetMapping("/")
    public List<Message> findAll() {
        return StreamSupport.stream(this.messages.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findById(@PathVariable int id) {
        var message = this.messages.findById(id);
        if (message.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Message not found");
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(message.get());
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Message> create(@Valid @RequestBody Message message) {
        if (message.getText().isEmpty()) {
            throw new NullPointerException("Empty message");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.messages.save(message));
    }

    @PutMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Message> update(@Valid @RequestBody Message message) {
        if (message.getText().isEmpty()) {
            throw new NullPointerException("Empty message");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.messages.save(message));
    }

    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public ResponseEntity<Void> delete(@Valid @PathVariable int id) {
        if (id == 0) {
            throw new NullPointerException("Empty id");
        }
        var message = new Message();
        message.setId(id);
        this.messages.delete(message);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/")
    public Message patch(@RequestBody Message message)
            throws InvocationTargetException, IllegalAccessException {
        var newMessage = messages.findById(message.getId());
        if (newMessage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var patchService = new PatchService<Message>();
        messages.save(patchService.getPatch(newMessage.get(), message));
        return newMessage.get();
    }
}
