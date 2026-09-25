package com.vidyasahay.vidyasahay.dto.request;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Payload used by GOVERNMENT / BANK to verify or reject an uploaded student document.
 * Only VERIFIED and REJECTED are accepted; PENDING is rejected in the service layer.
 */
public record UpdateDocumentStatusRequest(

        @NotNull(message = "Verification status is required")
        VerificationStatus verificationStatus
) {}
