package com.homework.library.service.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException() {
        super("Requested booking is not found.");
    }
}
