package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record DocumentTypeResponse(
        UUID documentTypeId,
        String name,
        String description
) {}
