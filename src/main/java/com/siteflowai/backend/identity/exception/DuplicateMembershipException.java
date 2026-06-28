package com.siteflowai.backend.identity.exception;

/**
 * Thrown when a user already has a membership in the target company.
 * HTTP mapping (409 Conflict) is deferred until identity controllers are introduced.
 */
public class DuplicateMembershipException extends RuntimeException {

    public DuplicateMembershipException(String message) {
        super(message);
    }
}
