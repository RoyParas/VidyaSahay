package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;
import java.math.BigDecimal;

public record CourseSummaryResponse(
        UUID id,
        String name,
        Integer durationYears,
        BigDecimal fees,
        UUID professionId,
        String professionName
) {}
