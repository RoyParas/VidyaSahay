package com.vidyasahay.vidyasahay.dto.response.scholarshipScheme;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.List;

import com.vidyasahay.vidyasahay.dto.response.SchemeDocumentRequirement;

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
        List<SchemeDocumentRequirement> documentRequirements,
        List<String> eligibleProfessions,
        List<String> eligibleCategories,

        double scholarshipAmount,
        String amountType,
        String paymentFrequency,

        double totalSchemeBudget
) {
    public ScholarshipSchemeDetailedResponseDTO(UUID scholarshipSchemeId, String scholarshipName,
            String scholarshipType, String academicYear, String status, LocalDate startDate, LocalDate endDate,
            int applierMinAge, int applierMaxAge, long maxFamilyAnnualIncome, double minPercentageCriteria,
            List<String> requiredDocuments, List<String> eligibleProfessions, List<String> eligibleCategories,
            double scholarshipAmount, String amountType, String paymentFrequency, double totalSchemeBudget) {
        this(scholarshipSchemeId, scholarshipName, scholarshipType, academicYear, status, startDate, endDate,
                applierMinAge, applierMaxAge, maxFamilyAnnualIncome, minPercentageCriteria, requiredDocuments,
                List.of(), eligibleProfessions, eligibleCategories, scholarshipAmount, amountType,
                paymentFrequency, totalSchemeBudget);
    }
}
