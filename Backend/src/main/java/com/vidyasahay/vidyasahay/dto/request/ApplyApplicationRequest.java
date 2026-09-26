package com.vidyasahay.vidyasahay.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public record ApplyApplicationRequest(
        String applicationType,
        UUID schemeId,
        BigDecimal requestedLoanAmount,
        BigDecimal academicPercentage,
        String loanPurpose,
        Integer repaymentTenureYears,
        String coBorrowerName,
        BigDecimal coBorrowerIncome,
        MultipartFile[] documents,
        UUID[] documentTypeIds
) {
    public ApplyApplicationRequest(String applicationType, UUID schemeId, MultipartFile[] documents, UUID[] documentTypeIds) {
        this(applicationType, schemeId, null, null, null, null, null, null, documents, documentTypeIds);
    }
}
