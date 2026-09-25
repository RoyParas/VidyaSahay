package com.vidyasahay.vidyasahay.dto.request.loanScheme;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.DisbursementType;
import com.vidyasahay.vidyasahay.enums.InterestType;

public record CreateLoanSchemeRequestDTO(
        @NotBlank(message = "Scheme name is required")
        @Size(max = 200, message = "Scheme name must not exceed 200 characters")
        String schemeName,

        @NotNull(message = "Effective-from date is required")
        @FutureOrPresent(message = "Effective-from date cannot be in the past")
        LocalDate effectiveFrom,

        @NotNull(message = "Effective-to date is required")
        @FutureOrPresent(message = "Effective-to date cannot be in the past")
        LocalDate effectiveTo,

        @NotNull(message = "Minimum loan amount is required")
        @DecimalMin(value = "0.01", message = "Minimum loan amount must be greater than zero")
        @Digits(integer = 13, fraction = 2, message = "Minimum loan amount must have at most 13 integer digits and 2 decimal places")
        BigDecimal minLoanAmount,

        @NotNull(message = "Maximum loan amount is required")
        @DecimalMin(value = "0.01", message = "Maximum loan amount must be greater than zero")
        @Digits(integer = 13, fraction = 2, message = "Maximum loan amount must have at most 13 integer digits and 2 decimal places")
        BigDecimal maxLoanAmount,

        @NotNull(message = "Interest type is required")
        InterestType interestType,

        @DecimalMin(value = "0.0000", message = "Minimum interest rate cannot be negative")
        @Digits(integer = 3, fraction = 4, message = "Minimum interest rate must have at most 3 integer digits and 4 decimal places")
        BigDecimal minInterestRate,

        @NotNull(message = "Maximum interest rate is required")
        @DecimalMin(value = "0.0000", message = "Maximum interest rate cannot be negative")
        @Digits(integer = 3, fraction = 4,message = "Maximum interest rate must have at most 3 integer digits and 4 decimal places")
        BigDecimal maxInterestRate,

        @NotNull(message = "Disbursement type is required")
        DisbursementType disbursementType,

        @NotNull(message = "Minimum applicant age is required")
        @Min(value = 18, message = "Minimum applicant age must be at least 18")
        @Max(value = 100, message = "Minimum applicant age must not exceed 100")
        Integer applierMinAge,

        @NotNull(message = "Maximum applicant age is required")
        @Min(value = 18, message = "Maximum applicant age must be at least 18")
        @Max(value = 100, message = "Maximum applicant age must not exceed 100")
        Integer applierMaxAge,

        @NotNull(message = "Co-borrower requirement is required")
        Boolean coBorrowerRequired,

        @Min(value = 300, message = "Minimum credit score must be at least 300")
        @Max(value = 900, message = "Minimum credit score must not exceed 900")
        Integer minCreditScore,

        @NotEmpty(message = "At least one required document is required")
        Set<UUID> requiredDocumentIds,

        @NotEmpty(message = "At least one eligible profession is required")
        Set<UUID> eligibleProfessionIds,

        @NotNull(message = "Minimum repayment tenure is required")
        @Min(value = 1, message = "Minimum repayment tenure must be at least 1 year")
        Integer minTenureForRepayment,

        @NotNull(message = "Maximum repayment tenure is required")
        @Min(value = 1, message = "Maximum repayment tenure must be at least 1 year")
        Integer maxTenureForRepayment,

        @NotNull(message = "Prepayment allowance is required")
        Boolean prepaymentAllowed,

        @DecimalMin(value = "0.00", message = "Foreclosure charges cannot be negative")
        @Digits(integer = 8, fraction = 2, message = "Foreclosure charges must have at most 8 integer digits and 2 decimal places")
        BigDecimal foreclosureCharges,

        @NotNull(message = "Course-period inclusion is required")
        Boolean coursePeriodIncluded,

        @NotNull(message = "Additional moratorium months are required")
        @Min(value = 0, message = "Additional moratorium months cannot be negative")
        @Max(value = 120, message = "Additional moratorium months must not exceed 120")
        Integer additionalMonths
) {
}