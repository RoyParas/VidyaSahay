package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.RoleName;

public record AuthenticatedUserResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        RoleName role,
        boolean mustChangePassword,
        boolean profileCompleted
) {}
