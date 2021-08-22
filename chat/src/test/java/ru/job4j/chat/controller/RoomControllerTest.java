package ru.job4j.chat.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.chat.ChatApplication;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.domain.Room;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ChatApplication.class)
@AutoConfigureMockMvc
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomController rooms;

    @Test
    public void whenFindAll() throws Exception {
        var room1 = Room.of("room1");
        var room2 = Room.of("room2");
        room1.setId(1);
        room2.setId(2);
        when(rooms.findAll()).thenReturn(Arrays.asList(room1, room2));
        mockMvc.perform(get("/rooms/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("room1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("room2")));
    }

    @Test
    public void whenAdd() throws Exception {
        var room = Room.of("room1");
        when(rooms.create(any(Room.class))).thenReturn(new ResponseEntity<>(room, HttpStatus.OK));
        mockMvc.perform(post("/rooms/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"room1\"}"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Room> arg = ArgumentCaptor.forClass(Room.class);
        verify(rooms).create(arg.capture());
        assertEquals(arg.getValue().getName(), "room1");
    }

    @Test
    public void whenUpdate() throws Exception {
        when(rooms.update(any(Room.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        mockMvc.perform(put("/rooms/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"name\":\"room2\"}"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(rooms, times(1)).update(any(Room.class));
        ArgumentCaptor<Room> arg = ArgumentCaptor.forClass(Room.class);
        verify(rooms).update(arg.capture());
        assertEquals("room2", arg.getValue().getName());
    }

    @Test
    public void whenFindById() throws Exception {
        var room = Room.of("room1");
        room.setId(1);
        when(rooms.findById(anyInt())).thenReturn(new ResponseEntity<>(room, HttpStatus.OK));
        mockMvc.perform(get("/rooms/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("room1")));
    }

    @Test
    public void whenDelete() throws Exception {
        when(rooms.delete(anyInt())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        mockMvc.perform(delete("/rooms/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void whenFindByMessagesByRoomId() throws Exception {
        var room = Room.of("room1");
        room.setId(1);
        var person = Person.of(1, "user", "pass");
        var message1 = Message.of(1, "text1", person);
        var message2 = Message.of(2, "text2", person);
        message1.setRoom(room);
        message2.setRoom(room);
        when(rooms.getMessagesByRoom(anyInt())).thenReturn(new ResponseEntity<>(
                List.of(message1, message2), HttpStatus.OK));
        mockMvc.perform(get("/rooms/1/messages"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].text", is("text1")))
                .andExpect(jsonPath("$[0].room.name", is("room1")))
                .andExpect(jsonPath("$[0].person.id", is(1)))
                .andExpect(jsonPath("$[0].person.login", is("user")))
                .andExpect(jsonPath("$[0].person.password", is("pass")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].text", is("text2")));
    }

    @Test
    public void whenCreateMessage() throws Exception {
        var room = Room.of("room1");
        var person = Person.of(1, "user", "pass");
        var message = Message.of(1, "text1", person);
        message.setRoom(room);
        when(rooms.createMessage(anyInt(), anyInt(), anyString())).thenReturn(
                new ResponseEntity<>(message, HttpStatus.OK));
        mockMvc.perform(post("/rooms/1/create")
                        .param("uid", "1")
                        .param("text", "text1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteMessage() throws Exception {
        when(rooms.deleteMessage(anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        mockMvc.perform(delete("/rooms/1/delete/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}