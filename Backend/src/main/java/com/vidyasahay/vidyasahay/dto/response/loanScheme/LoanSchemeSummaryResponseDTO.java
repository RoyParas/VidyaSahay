package com.vidyasahay.vidyasahay.dto.response.loanScheme;

import java.math.BigDecimal;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.InterestType;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;


public record LoanSchemeSummaryResponseDTO(
        UUID loanSchemeId,
        UUID bankId,
        String schemeName,
        InterestType interestType,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        SchemeStatus status
) {
    public LoanSchemeSummaryResponseDTO(UUID loanSchemeId, UUID bankId, String schemeName,
            InterestType interestType, int minAmount, int maxAmount, SchemeStatus status) {
        this(loanSchemeId, bankId, schemeName, interestType, BigDecimal.valueOf(minAmount),
                BigDecimal.valueOf(maxAmount), status);
    }
}
