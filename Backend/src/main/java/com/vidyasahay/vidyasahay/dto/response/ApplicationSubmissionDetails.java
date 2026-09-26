package com.vidyasahay.vidyasahay.dto.response;

import java.math.BigDecimal;

public record ApplicationSubmissionDetails(
        BigDecimal requestedLoanAmount,
        BigDecimal academicPercentage,
        String loanPurpose,
        Integer repaymentTenureYears,
        String coBorrowerName,
        BigDecimal coBorrowerIncome
) {
}
