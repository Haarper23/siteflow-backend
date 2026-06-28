package com.siteflowai.backend.identity.dto;

import com.siteflowai.backend.identity.domain.AccountStatus;
import com.siteflowai.backend.identity.domain.AppUser;

import java.time.Instant;
import java.util.UUID;

/**
 * Outward-facing view of a user. Never exposes the password hash.
 */
public record UserResponse(
        UUID id,
        String email,
        String fullName,
        AccountStatus status,
        Instant createdAt
) {

    public static UserResponse from(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
