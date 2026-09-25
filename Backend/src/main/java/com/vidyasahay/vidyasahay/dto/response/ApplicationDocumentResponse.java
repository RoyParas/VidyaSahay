package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

import java.time.LocalDateTime;

public record ApplicationDocumentResponse(
        UUID applicationDocumentId,
        UUID studentDocumentId,
        UUID documentTypeId,
        String documentTypeName,
        String fileName,
        VerificationStatus verificationStatus,
        String verificationRemark,
        UUID verifiedByName,
        LocalDateTime verifiedAt
) {}
