package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record SchemeDocumentRequirement(
        UUID documentTypeId,
        String name,
        String description
) {
}
