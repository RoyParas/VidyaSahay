package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateInstituteContactRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String mobile
) {
} 

