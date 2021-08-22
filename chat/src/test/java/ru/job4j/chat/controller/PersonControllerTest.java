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
import ru.job4j.chat.domain.Person;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest(classes = ChatApplication.class)
@AutoConfigureMockMvc
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonController persons;

    @Test
    public void whenFindAll() throws Exception {
        var person1 = Person.of(1, "user", "pass");
        var person2 = Person.of(2, "admin", "admin");

        when(persons.findAll()).thenReturn(List.of(person1, person2));
        mockMvc.perform(get("/persons/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].login", is("user")))
                .andExpect(jsonPath("$[0].password", is("pass")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].login", is("admin")))
                .andExpect(jsonPath("$[1].password", is("admin")));
    }

    @Test
    public void whenAdd() throws Exception {
        var person = Person.of(1, "user2", "pass");
        when(persons.create(any(Person.class))).thenReturn(
                new ResponseEntity<>(person, HttpStatus.OK));
        mockMvc.perform(post("/persons/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"user2\",\"password\":\"pass\"}"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> arg = ArgumentCaptor.forClass(Person.class);
        verify(persons).create(arg.capture());
        assertEquals(arg.getValue().getLogin(), "user2");
        assertEquals(arg.getValue().getPassword(), "pass");
    }

    @Test
    public void whenUpdate() throws Exception {
        when(persons.update(any(Person.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        mockMvc.perform(put("/persons/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"login\":\"user\",\"password\":\"pass2\"}"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> arg = ArgumentCaptor.forClass(Person.class);
        verify(persons).update(arg.capture());
        assertEquals("user", arg.getValue().getLogin());
        assertEquals("pass2", arg.getValue().getPassword());
    }

    @Test
    public void whenFindById() throws Exception {
        var person = Person.of(1, "user", "pass");
        when(persons.findById(anyInt())).thenReturn(new ResponseEntity<>(person, HttpStatus.OK));
        mockMvc.perform(get("/persons/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.login", is("user")))
                .andExpect(jsonPath("$.password", is("pass")));
    }

    @Test
    public void whenDelete() throws Exception {
        when(persons.delete(anyInt())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        mockMvc.perform(delete("/persons/2"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}