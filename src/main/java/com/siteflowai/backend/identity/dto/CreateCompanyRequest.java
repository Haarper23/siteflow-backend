package com.siteflowai.backend.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCompanyRequest(

        @NotBlank
        @Size(max = 150)
        String name
) {
}
