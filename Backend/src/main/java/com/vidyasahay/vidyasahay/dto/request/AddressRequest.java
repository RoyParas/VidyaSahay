package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank String country,
        @NotBlank String state,
        @NotBlank String district,
        @NotBlank String city
) {}
