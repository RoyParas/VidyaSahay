package com.vidyasahay.vidyasahay.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentDocumentResponse(

        UUID id,

        UUID studentId,

        UUID documentTypeId,

        String documentTypeName,

        String documentTypeDescription,

        String fileName,

        String fileUrl,

        String verificationStatus,

        UUID verifiedByUserId,

        LocalDateTime verifiedAt

) {
}