package com.vidyasahay.vidyasahay.dto.response;



public record EligibilityCriterionResponse(
        String criterion,
        String expectedValue,
        String actualValue,
        boolean passed,
        String reason
) {}
