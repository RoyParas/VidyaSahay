package com.vidyasahay.vidyasahay.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.RoleName;

public record UserSummaryResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String mobile,
        RoleName role,
        boolean active,
        boolean mustChangePassword,
        LocalDateTime createdAt
) {}
