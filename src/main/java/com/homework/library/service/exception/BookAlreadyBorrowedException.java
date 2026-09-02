package com.homework.library.service.exception;

public class BookAlreadyBorrowedException extends RuntimeException {

    public BookAlreadyBorrowedException(Long bookId, Long borrowerId) {
        super("Book with id " + bookId + " is already borrowed by another borrower with id " + borrowerId);
    }
}
