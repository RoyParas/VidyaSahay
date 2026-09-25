package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record SchemeSummaryResponse(
        UUID schemeId,
        String schemeName,
        String providerName
) {}
