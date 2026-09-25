package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

public record StudentSummaryResponse(
        UUID studentId,
        String firstName,
        String lastName,
        String email,
        String mobile,
        String maskedAadharNumber,
        String courseName,
        String instituteName
) {}
