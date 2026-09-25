package com.vidyasahay.vidyasahay.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

public record StudentVerificationStatusResponse(
        UUID verificationId,
        VerificationStatus status,
        String remark,
        String verifiedByName,
        LocalDateTime verifiedAt,
        LocalDateTime updatedAt
) {}
