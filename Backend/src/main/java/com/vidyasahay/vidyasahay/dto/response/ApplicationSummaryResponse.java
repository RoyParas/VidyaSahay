package com.vidyasahay.vidyasahay.dto.response;

import com.vidyasahay.vidyasahay.enums.ApplicationStatus;
import com.vidyasahay.vidyasahay.enums.ApplicationType;

import java.math.BigDecimal;
import java.util.UUID;

public record ApplicationSummaryResponse(
        UUID id,
        ApplicationType applicationType,
        UUID schemeId,
        String schemeName,
        UUID studentId,
        String studentFirstName,
        String studentLastName,
        ApplicationStatus status,
        BigDecimal approvedAmount
) {
}