package com.vidyasahay.vidyasahay.dto.response.loanScheme;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.DisbursementType;
import com.vidyasahay.vidyasahay.enums.InterestType;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;

public record LoanSchemeDetailedResponseDTO(
        UUID loanSchemeId,
        UUID bankId,
        String schemeName,
        InterestType interestType,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        SchemeStatus status,

        // Detailed fields
        LocalDate effectiveFrom,
        LocalDate effectiveTo,

        // Null for FIXED interest type
        BigDecimal minInterestRate,

        BigDecimal maxInterestRate,
        DisbursementType disbursementType,

        Integer applierMinAge,
        Integer applierMaxAge,
        boolean coBorrowerRequired,

        // Null when coBorrowerRequired is false
        Integer minCreditScore,

        List<String> requiredDocumentIds,
        List<String> eligibleProfessionIds,

        Integer minTenureForRepayment,
        Integer maxTenureForRepayment,
        boolean prepaymentAllowed,

        // Null when prepaymentAllowed is false
        BigDecimal foreclosureCharges,

        boolean coursePeriodIncluded,
        Integer additionalMonths
) {
}
