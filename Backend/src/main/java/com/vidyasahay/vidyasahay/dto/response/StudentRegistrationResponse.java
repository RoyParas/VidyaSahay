package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.RoleName;

public record StudentRegistrationResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String mobile,
        RoleName role,
        boolean mustCompleteProfile,
        String message
) {
}