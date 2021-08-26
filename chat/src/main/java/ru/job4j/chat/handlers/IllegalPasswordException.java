package ru.job4j.chat.handlers;

public class IllegalPasswordException extends Exception {

    public IllegalPasswordException(String errorMessage) {
        super(errorMessage);
    }
}
