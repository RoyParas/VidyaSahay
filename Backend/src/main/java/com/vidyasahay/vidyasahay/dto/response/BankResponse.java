package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;
import java.time.LocalDateTime;

public record BankResponse(
        UUID bankId,
        UUID userId,
        String bankName,
        String email,
        String mobile,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
