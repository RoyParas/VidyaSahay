package com.vidyasahay.vidyasahay.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DisbursementSummaryResponse(
        UUID id,
        UUID applicationId,
        String applicationType,
        UUID studentId,
        String studentFirstName,
        String studentLastName,
        BigDecimal amount,
        LocalDate disbursementDate,
        String status,
        String remark
) {
}