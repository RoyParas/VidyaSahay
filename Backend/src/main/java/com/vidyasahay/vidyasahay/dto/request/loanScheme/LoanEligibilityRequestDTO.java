package com.vidyasahay.vidyasahay.dto.request.loanScheme;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanEligibilityRequestDTO(

        @NotNull(message = "Required loan amount is required")
        @DecimalMin(
                value = "0.01",
                inclusive = true,
                message = "Required loan amount must be greater than zero"
        )
        @Digits(
                integer = 13,
                fraction = 2,
                message = "Required loan amount must contain at most 13 integer digits and 2 decimal places"
        )
        BigDecimal requiredLoanAmount

) {
}
