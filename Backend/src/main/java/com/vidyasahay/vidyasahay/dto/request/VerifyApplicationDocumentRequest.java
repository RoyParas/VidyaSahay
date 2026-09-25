package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.NotNull;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

import jakarta.validation.constraints.NotBlank;

public record VerifyApplicationDocumentRequest(
        @NotNull VerificationStatus status,
        @NotBlank String remark
) {}
