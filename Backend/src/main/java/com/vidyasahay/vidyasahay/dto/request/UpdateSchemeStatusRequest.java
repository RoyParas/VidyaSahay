package com.vidyasahay.vidyasahay.dto.request;

import com.vidyasahay.vidyasahay.enums.SchemeStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateSchemeStatusRequest(
        @NotNull SchemeStatus status
) {}
