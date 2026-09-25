package com.vidyasahay.vidyasahay.dto.request.loanScheme;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CheckLoanEligibilityRequest(
        @NotNull BigDecimal requestedAmount,
        Integer creditScore,
        boolean coBorrowerAvailable
) {}
