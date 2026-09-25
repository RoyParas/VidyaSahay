package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record DocumentListResponse(
        UUID documentId,
        String documentName
) {
}
