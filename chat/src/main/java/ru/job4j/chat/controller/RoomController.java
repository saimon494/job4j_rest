package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.domain.Room;
import ru.job4j.chat.repository.MessageRepository;
import ru.job4j.chat.repository.PersonRepository;
import ru.job4j.chat.repository.RoomRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private final RoomRepository rooms;
    @Autowired
    private final PersonRepository persons;
    @Autowired
    private final MessageRepository messages;

    public RoomController(
            final RoomRepository rooms,
            final PersonRepository persons,
            MessageRepository messages) {
        this.rooms = rooms;
        this.persons = persons;
        this.messages = messages;
    }

    @GetMapping("/")
    public List<Room> findAll() {
        return StreamSupport.stream(this.rooms.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findById(@PathVariable int id) {
        var room = this.rooms.findById(id);
        if (room.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Room not found");
        }
        return new ResponseEntity<>(room.orElse(new Room()), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Room> create(@RequestBody Room room) {
        if (room.getName().isEmpty()) {
            throw new NullPointerException("Empty room");
        }
        return new ResponseEntity<>(
                this.rooms.save(room), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Room room) {
        if (room.getName().isEmpty()) {
            throw new NullPointerException("Empty room");
        }
        this.rooms.save(room);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (id == 0) {
            throw new NullPointerException("Empty id");
        }
        var room = new Room();
        room.setId(id);
        this.rooms.delete(room);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<Message>> getMessagesByRoom(@PathVariable int id) {
        var room = this.rooms.findById(id);
        if (room.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Room not found");
        }
        return new ResponseEntity<>(this.messages.getByRoomId(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/create")
    public ResponseEntity<Message> createMessage(@PathVariable int id,
                                                 @RequestParam(value = "uid") int uid,
                                                 @RequestParam(value = "text") String text) {
        var person = this.persons.findById(uid);
        var room = this.rooms.findById(id);
        if (person.isEmpty() || room.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Person or room not found");
        }
        var newMsg = Message.of(0, text, person.get());
        newMsg.setRoom(room.get());
        return new ResponseEntity<>(this.messages.save(newMsg), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete/{mid}")
    public ResponseEntity<Void> deleteMessage(@PathVariable int id,
                                              @PathVariable int mid) {
        if (mid == 0) {
            throw new NullPointerException("Empty id");
        }
        var message = new Message();
        message.setId(mid);
        this.messages.delete(message);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
