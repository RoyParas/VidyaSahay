package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserStatusRequest(
        boolean active,
        @NotBlank String reason
) {}
