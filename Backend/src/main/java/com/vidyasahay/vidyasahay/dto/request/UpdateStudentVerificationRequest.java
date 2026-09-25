package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.NotNull;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateStudentVerificationRequest(
        @NotNull(message = "Status is required")
        VerificationStatus status,

        @NotBlank(message = "Remark is required")
        @Size(max = 1000, message = "Remark cannot exceed 1000 characters")
        String remark
) {
}
