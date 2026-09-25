package com.vidyasahay.vidyasahay.dto.request;

import com.vidyasahay.vidyasahay.enums.DisbursementStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateDisbursementStatusRequest(
        @NotNull DisbursementStatus status,
        String transactionReference,
        @Size(max=1000) String remark
) {}
