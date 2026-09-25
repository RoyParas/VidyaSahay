package com.vidyasahay.vidyasahay.dto.response.loanScheme;



public record LoanSchemeEligibilityResponse(
        Integer minAge,
        Integer maxAge,
        boolean coBorrowerRequired,
        Integer minCreditScore
) {}
