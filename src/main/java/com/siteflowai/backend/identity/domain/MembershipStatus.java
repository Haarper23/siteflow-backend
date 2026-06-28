package com.siteflowai.backend.identity.domain;

/**
 * Lifecycle state of a user's membership within a single company.
 */
public enum MembershipStatus {
    ACTIVE,
    INVITED,
    SUSPENDED
}
