package com.vidyasahay.vidyasahay.dto.response.loanScheme;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.response.SchemeDocumentRequirement;
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

        // Minimum rate is stored for both FIXED and FLOATING schemes
        BigDecimal minInterestRate,

        BigDecimal maxInterestRate,
        DisbursementType disbursementType,

        Integer applierMinAge,
        Integer applierMaxAge,
        boolean coBorrowerRequired,

        // Null when coBorrowerRequired is false
        Integer minCreditScore,

        List<String> requiredDocumentIds,
        List<SchemeDocumentRequirement> documentRequirements,
        List<String> eligibleProfessionIds,

        Integer minTenureForRepayment,
        Integer maxTenureForRepayment,
        boolean prepaymentAllowed,

        // Null when prepaymentAllowed is false
        BigDecimal foreclosureCharges,

        boolean coursePeriodIncluded,
        Integer additionalMonths
) {
    public LoanSchemeDetailedResponseDTO(UUID loanSchemeId, UUID bankId, String schemeName, InterestType interestType,
            BigDecimal minAmount, BigDecimal maxAmount, SchemeStatus status, LocalDate effectiveFrom,
            LocalDate effectiveTo, BigDecimal minInterestRate, BigDecimal maxInterestRate,
            DisbursementType disbursementType, Integer applierMinAge, Integer applierMaxAge,
            boolean coBorrowerRequired, Integer minCreditScore, List<String> requiredDocumentIds,
            List<String> eligibleProfessionIds, Integer minTenureForRepayment, Integer maxTenureForRepayment,
            boolean prepaymentAllowed, BigDecimal foreclosureCharges, boolean coursePeriodIncluded,
            Integer additionalMonths) {
        this(loanSchemeId, bankId, schemeName, interestType, minAmount, maxAmount, status, effectiveFrom,
                effectiveTo, minInterestRate, maxInterestRate, disbursementType, applierMinAge, applierMaxAge,
                coBorrowerRequired, minCreditScore, requiredDocumentIds, List.of(), eligibleProfessionIds,
                minTenureForRepayment, maxTenureForRepayment, prepaymentAllowed, foreclosureCharges,
                coursePeriodIncluded, additionalMonths);
    }
}
