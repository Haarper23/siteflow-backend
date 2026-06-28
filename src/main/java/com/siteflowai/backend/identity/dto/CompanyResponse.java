package com.siteflowai.backend.identity.dto;

import com.siteflowai.backend.identity.domain.Company;

import java.time.Instant;
import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String name,
        Instant createdAt
) {

    public static CompanyResponse from(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getCreatedAt()
        );
    }
}
