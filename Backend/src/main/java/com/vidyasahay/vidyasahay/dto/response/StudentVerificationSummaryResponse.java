package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

import java.time.LocalDateTime;

public record StudentVerificationSummaryResponse(
        UUID verificationId,
        UUID studentId,
        String studentName,
        String courseName,
        String categoryCode,
        VerificationStatus status,
        LocalDateTime createdAt
) {}
