package com.siteflowai.backend.identity.domain;

/**
 * Fixed set of membership roles. Mirrors the seeded {@code role} reference table;
 * the {@code role_code} column on {@code company_membership} has a foreign key to it.
 */
public enum RoleCode {
    OWNER,
    ADMIN,
    MANAGER,
    WORKER
}
