package com.siteflowai.backend.identity.exception;

/**
 * Thrown when attempting to create a user with an email that is already registered.
 * HTTP mapping (409 Conflict) is deferred until identity controllers are introduced.
 */
public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Email already in use: " + email);
    }
}
