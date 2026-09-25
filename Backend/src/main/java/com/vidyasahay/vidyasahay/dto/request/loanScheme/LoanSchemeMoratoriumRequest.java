package com.vidyasahay.vidyasahay.dto.request.loanScheme;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record LoanSchemeMoratoriumRequest(
        boolean coursePeriodIncluded,
        @NotNull @Min(0) Integer additionalMonths
) {}
