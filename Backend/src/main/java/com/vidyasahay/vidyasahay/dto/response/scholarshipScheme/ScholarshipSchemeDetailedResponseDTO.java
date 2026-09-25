package com.vidyasahay.vidyasahay.dto.response.scholarshipScheme;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ScholarshipSchemeDetailedResponseDTO(
        UUID scholarshipSchemeId,
        String scholarshipName,
        String scholarshipType,
        String academicYear,
        String status,

        LocalDate startDate,
        LocalDate endDate,

        int applierMinAge,
        int applierMaxAge,

        long maxFamilyAnnualIncome,
        double minPercentageCriteria,

        List<String> requiredDocuments,
        List<String> eligibleProfessions,
        List<String> eligibleCategories,

        double scholarshipAmount,
        String amountType,
        String paymentFrequency,

        double totalSchemeBudget
) {
}