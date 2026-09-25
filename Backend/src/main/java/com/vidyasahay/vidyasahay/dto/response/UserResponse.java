package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.RoleName;

import java.time.LocalDateTime;

public record UserResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String mobile,
        RoleName role,
        boolean active,
        boolean mustChangePassword,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
