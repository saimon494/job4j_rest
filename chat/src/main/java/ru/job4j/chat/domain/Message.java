package ru.job4j.chat.domain;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text;
    private Timestamp time;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id", foreignKey = @ForeignKey(name = "PERSON_ID_FK"))
    private Person person;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "ROOM_ID_FK"))
    private Room room;

    public Message() {
    }

    public static Message of(int id, String text, Person person) {
        var message = new Message();
        message.id = id;
        message.text = text;
        message.person = person;
        message.time = new Timestamp(System.currentTimeMillis());
        return message;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime() {
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Message{"
                + "id=" + id
                + ", text='" + text + '\''
                + ", time=" + time
                + ", person=" + person
                + ", room=" + room
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return id == message.id
                && Objects.equals(text, message.text)
                && Objects.equals(time, message.time)
                && Objects.equals(person, message.person)
                && Objects.equals(room, message.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, time, person, room);
    }
}
