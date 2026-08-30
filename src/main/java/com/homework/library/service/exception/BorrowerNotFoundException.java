package com.homework.library.service.exception;

public class BorrowerNotFoundException extends RuntimeException {

    public BorrowerNotFoundException() {
        super("Requested borrower not found.");
    }
}
