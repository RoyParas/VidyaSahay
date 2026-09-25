package com.vidyasahay.vidyasahay.dto.request.scholarshipScheme;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.PaymentFrequency;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.enums.ScholarshipAmountType;
import com.vidyasahay.vidyasahay.enums.ScholarshipType;

import java.time.LocalDate;
import java.util.Set;

public record UpdateScholarshipSchemeRequestDTO(
        String schemeName,
        ScholarshipType scholarshipType,
        String academicYear,
        LocalDate startDate,
        LocalDate endDate,
        Integer applierMinAge,
        Integer applierMaxAge,
        Long maxFamilyAnnualIncome,
        Double minPercentageCriteria,
        Set<UUID> requiredDocumentIds,
        Set<UUID> eligibleProfessionIds,
        Set<UUID> eligibleCategoriesIds,
        Double scholarshipAmount,
        ScholarshipAmountType amountType,
        PaymentFrequency paymentFrequency,
        Double totalSchemeBudget,
        SchemeStatus status
) {}
