package com.siteflowai.backend.identity.dto;

import com.siteflowai.backend.identity.domain.CompanyMembership;
import com.siteflowai.backend.identity.domain.MembershipStatus;
import com.siteflowai.backend.identity.domain.RoleCode;

import java.time.Instant;
import java.util.UUID;

public record MembershipResponse(
        UUID id,
        UUID companyId,
        UUID userId,
        RoleCode role,
        MembershipStatus status,
        Instant createdAt
) {

    public static MembershipResponse from(CompanyMembership membership) {
        return new MembershipResponse(
                membership.getId(),
                membership.getCompanyId(),
                membership.getUserId(),
                membership.getRole(),
                membership.getStatus(),
                membership.getCreatedAt()
        );
    }
}
