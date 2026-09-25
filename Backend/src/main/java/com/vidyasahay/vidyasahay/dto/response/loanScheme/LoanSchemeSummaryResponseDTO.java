package com.vidyasahay.vidyasahay.dto.response.loanScheme;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.InterestType;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;


public record LoanSchemeSummaryResponseDTO(
        UUID loanSchemeId,
        UUID bankId,
        String schemeName,
        InterestType interestType,
        int minAmount,
        int maxAmount,
        SchemeStatus status
) {
}
