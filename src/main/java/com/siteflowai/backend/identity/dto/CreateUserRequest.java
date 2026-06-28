package com.siteflowai.backend.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        // BCrypt only considers the first 72 bytes; cap the input accordingly.
        @NotBlank
        @Size(min = 8, max = 72)
        String password,

        @Size(max = 150)
        String fullName
) {
}
