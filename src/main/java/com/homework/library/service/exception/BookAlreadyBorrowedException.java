package com.homework.library.service.exception;

public class BookAlreadyBorrowedException extends RuntimeException {

    public BookAlreadyBorrowedException(Long bookId) {
        super("Book with id " + bookId + " is already borrowed by someone.");
    }
}
