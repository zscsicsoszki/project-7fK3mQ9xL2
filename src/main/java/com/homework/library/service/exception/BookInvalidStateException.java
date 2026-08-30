package com.homework.library.service.exception;

public class BookInvalidStateException extends RuntimeException {

    public BookInvalidStateException() {
        super("Book is already in the requested state.");
    }
}
