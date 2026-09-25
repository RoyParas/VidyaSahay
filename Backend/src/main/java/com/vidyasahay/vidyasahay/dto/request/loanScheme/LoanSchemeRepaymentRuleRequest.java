package com.vidyasahay.vidyasahay.dto.request.loanScheme;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record LoanSchemeRepaymentRuleRequest(
        @NotNull Integer minTenureYears,
        @NotNull Integer maxTenureYears,
        boolean prepaymentAllowed,
        @DecimalMin("0.00") BigDecimal foreclosureCharges
) {}
