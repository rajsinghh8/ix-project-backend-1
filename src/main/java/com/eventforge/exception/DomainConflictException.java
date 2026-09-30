package com.eventforge.exception;

/** Raised when a requested state conflicts with an existing domain resource. */
public class DomainConflictException extends RuntimeException {

    public DomainConflictException(String message) {
        super(message);
    }
}
