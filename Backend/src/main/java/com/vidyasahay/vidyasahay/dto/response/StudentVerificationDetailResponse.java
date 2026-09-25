package com.vidyasahay.vidyasahay.dto.response;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StudentVerificationDetailResponse(
        UUID verificationId,
        StudentDetailedResponse student,
        List<StudentDocumentResponse> documents,
        VerificationStatus status,
        String remark,
        String verifiedByName,
        LocalDateTime verifiedAt,
        LocalDateTime createdAt
) {}
