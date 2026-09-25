package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record LookupResponse(
        UUID id,
        String code,
        String name
) {}
