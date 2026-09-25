package com.vidyasahay.vidyasahay.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DisbursementDetailResponse(
        UUID id,
        UUID applicationId,
        String applicationType,
        UUID studentId,
        String studentFirstName,
        String studentLastName,
        UUID disbursedByUserId,
        String disbursedByUserFirstName,
        String disbursedByUserLastName,
        BigDecimal amount,
        LocalDate disbursementDate,
        String status,
        String remark
) {
}