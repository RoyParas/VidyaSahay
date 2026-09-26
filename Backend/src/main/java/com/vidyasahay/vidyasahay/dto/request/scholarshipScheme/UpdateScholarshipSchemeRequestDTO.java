package com.vidyasahay.vidyasahay.dto.request.scholarshipScheme;

import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import com.vidyasahay.vidyasahay.enums.PaymentFrequency;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.enums.ScholarshipAmountType;
import com.vidyasahay.vidyasahay.enums.ScholarshipType;

import java.time.LocalDate;
import java.util.Set;

public record UpdateScholarshipSchemeRequestDTO(
        @Size(max = 200, message = "Scheme name must not exceed 200 characters") String schemeName,
        ScholarshipType scholarshipType,
        String academicYear,
        LocalDate startDate,
        LocalDate endDate,
        @Min(0) @Max(100) Integer applierMinAge,
        @Min(0) @Max(100) Integer applierMaxAge,
        @Min(0) Long maxFamilyAnnualIncome,
        @DecimalMin("0.0") @DecimalMax("100.0") Double minPercentageCriteria,
        Set<UUID> requiredDocumentIds,
        Set<UUID> eligibleProfessionIds,
        Set<UUID> eligibleCategoriesIds,
        @DecimalMin("0.0") Double scholarshipAmount,
        ScholarshipAmountType amountType,
        PaymentFrequency paymentFrequency,
        @DecimalMin("0.0") Double totalSchemeBudget,
        SchemeStatus status
) {}
