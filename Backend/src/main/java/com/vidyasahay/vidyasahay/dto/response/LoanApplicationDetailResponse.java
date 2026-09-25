package com.vidyasahay.vidyasahay.dto.response;

import com.vidyasahay.vidyasahay.dto.response.ApplicationDocumentResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationHistoryResponse;
import com.vidyasahay.vidyasahay.dto.response.DisbursementSummaryResponse;
import com.vidyasahay.vidyasahay.enums.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LoanApplicationDetailResponse(
        UUID applicationId,
        StudentSummaryResponse student,
        SchemeSummaryResponse loanScheme,
        BigDecimal requestedAmount,
        BigDecimal approvedAmount,
        ApplicationStatus status,
        List<ApplicationDocumentResponse> documents,
        List<ApplicationHistoryResponse> history,
        List<DisbursementSummaryResponse> disbursements,
        LocalDateTime submittedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
