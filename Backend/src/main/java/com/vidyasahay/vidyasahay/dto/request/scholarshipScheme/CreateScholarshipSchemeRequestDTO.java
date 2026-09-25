package com.vidyasahay.vidyasahay.dto.request.scholarshipScheme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.PaymentFrequency;
import com.vidyasahay.vidyasahay.enums.ScholarshipAmountType;
import com.vidyasahay.vidyasahay.enums.ScholarshipType;

import java.time.LocalDate;
import java.util.Set;

public record CreateScholarshipSchemeRequestDTO(
    @NotBlank String schemeName,
    @NotNull ScholarshipType scholarshipType,
    @NotBlank String academicYear,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    int applierMinAge,
    int applierMaxAge,
    long maxFamilyAnnualIncome,
    double minPercentageCriteria,
    Set<UUID> requiredDocumentIds,
    Set<UUID> eligibleProfessionIds,
    Set<UUID> eligibleCategoriesIds,
    double scholarshipAmount,
    @NotNull ScholarshipAmountType amountType,
    @NotNull PaymentFrequency paymentFrequency,
    double totalSchemeBudget
) {}

