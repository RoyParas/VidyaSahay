package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record InstituteSummaryResponse(
        UUID id,
        String name,
        String location,
        Integer pincode
) {}
