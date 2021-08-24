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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.chat.ChatApplication;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.domain.Room;

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
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageController messages;

    @Test
    @WithMockUser
    public void whenFindAll() throws Exception {
        var room = Room.of("room");
        var person = Person.of(1, "user", "pass");
        var message1 = Message.of(1, "text1", person);
        var message2 = Message.of(2, "text2", person);
        message1.setRoom(room);
        message2.setRoom(room);
        when(messages.findAll()).thenReturn(List.of(message1, message2));
        mockMvc.perform(get("/messages/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].text", is("text1")))
                .andExpect(jsonPath("$[0].person.login", is("user")))
                .andExpect(jsonPath("$[0].person.password", is("pass")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].text", is("text2")));
    }

    @Test
    @WithMockUser
    public void whenAdd() throws Exception {
        var room = Room.of("room");
        var person = Person.of(1, "user", "pass");
        var message = Message.of(1, "text1", person);
        message.setRoom(room);

        when(messages.create(any(Message.class))).thenReturn(
                new ResponseEntity<>(message, HttpStatus.OK));
        mockMvc.perform(post("/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"text1\"}"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Message> arg = ArgumentCaptor.forClass(Message.class);
        verify(messages).create(arg.capture());
        assertEquals(arg.getValue().getText(), "text1");
    }

    @Test
    @WithMockUser
    public void whenUpdate() throws Exception {
        when(messages.update(any(Message.class))).thenReturn(
                new ResponseEntity<>(HttpStatus.OK));
        mockMvc.perform(put("/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"1\",\"text\":\"text2\"}"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Message> arg = ArgumentCaptor.forClass(Message.class);
        verify(messages).update(arg.capture());
        assertEquals("text2", arg.getValue().getText());
    }

    @Test
    @WithMockUser
    public void whenFindById() throws Exception {
        var room = Room.of("room");
        var person = Person.of(1, "user", "pass");
        var message = Message.of(1, "text1", person);
        message.setRoom(room);

        when(messages.findById(anyInt())).thenReturn(
                new ResponseEntity<>(message, HttpStatus.OK));
        mockMvc.perform(get("/messages/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("text1")))
                .andExpect(jsonPath("$.room.name", is("room")))
                .andExpect(jsonPath("$.person.id", is(1)))
                .andExpect(jsonPath("$.person.login", is("user")))
                .andExpect(jsonPath("$.person.password", is("pass")));
    }

    @Test
    @WithMockUser
    public void whenDelete() throws Exception {
        when(messages.delete(anyInt())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        mockMvc.perform(delete("/messages/2"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
