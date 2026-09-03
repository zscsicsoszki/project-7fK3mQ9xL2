package com.homework.library.service.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException() {
        super("Requested book is not found.");
    }
}
