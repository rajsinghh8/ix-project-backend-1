package com.eventforge.exception;

/** Raised when supplied login credentials cannot be authenticated. */
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException() {
        super("Invalid email or password");
    }
}
