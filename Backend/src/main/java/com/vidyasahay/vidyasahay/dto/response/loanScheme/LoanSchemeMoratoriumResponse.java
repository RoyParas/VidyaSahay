package com.vidyasahay.vidyasahay.dto.response.loanScheme;



public record LoanSchemeMoratoriumResponse(
        boolean coursePeriodIncluded,
        Integer additionalMonths
) {}
