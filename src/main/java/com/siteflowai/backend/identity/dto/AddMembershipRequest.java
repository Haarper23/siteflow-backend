package com.siteflowai.backend.identity.dto;

import com.siteflowai.backend.identity.domain.RoleCode;
import jakarta.validation.constraints.NotNull;

public record AddMembershipRequest(

        @NotNull
        java.util.UUID companyId,

        @NotNull
        java.util.UUID userId,

        @NotNull
        RoleCode role
) {
}
