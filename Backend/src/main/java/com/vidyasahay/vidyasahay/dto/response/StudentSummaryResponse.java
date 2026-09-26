package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

public record StudentSummaryResponse(
        UUID studentId,
        String firstName,
        String lastName,
        String email,
        String mobile,
        VerificationStatus verificationStatus,
        String courseName,
        String instituteName,
        UUID userId,
        boolean profileCompleted
) {}
