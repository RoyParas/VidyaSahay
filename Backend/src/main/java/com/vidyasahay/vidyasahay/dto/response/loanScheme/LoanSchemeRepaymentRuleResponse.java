package com.vidyasahay.vidyasahay.dto.response.loanScheme;

import java.math.BigDecimal;

public record LoanSchemeRepaymentRuleResponse(
        Integer minTenureYears,
        Integer maxTenureYears,
        boolean prepaymentAllowed,
        BigDecimal foreclosureCharges
) {}
