package com.siteflowai.backend.identity.exception;

/**
 * Thrown when a referenced identity resource does not exist.
 * HTTP mapping (404 Not Found) is deferred until identity controllers are introduced.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
