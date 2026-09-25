package com.vidyasahay.vidyasahay.dto.request.scholarshipScheme;

import java.math.BigDecimal;

public record ScholarshipEligibilityRequestDTO(
        BigDecimal annualFamilyIncome,
        BigDecimal academicPercentage
) {}
