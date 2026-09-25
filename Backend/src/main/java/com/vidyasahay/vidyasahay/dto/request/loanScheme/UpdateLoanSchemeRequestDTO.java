package com.vidyasahay.vidyasahay.dto.request.loanScheme;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.DisbursementType;
import com.vidyasahay.vidyasahay.enums.InterestType;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;

/**
 * Every field is nullable. A null field means "do not touch this value".
 * Only the fields that are actually sent in the request body are updated.
 *
 * The constraints below are range/format constraints only, they are applied
 * by Bean Validation when the field is present and skipped when it is null.
 * Cross-field business rules (dates, amounts, conditional fields) are checked
 * in LoanSchemeServiceImpl after the incoming values are merged with the
 * values already stored for the scheme.
 */
public record UpdateLoanSchemeRequestDTO(

        @Size(max = 200, message = "Scheme name must not exceed 200 characters")
        String schemeName,

        LocalDate effectiveFrom,

        LocalDate effectiveTo,

        @DecimalMin(value = "0.01", message = "Minimum loan amount must be greater than zero")
        @Digits(integer = 13, fraction = 2, message = "Minimum loan amount must have at most 13 integer digits and 2 decimal places")
        BigDecimal minLoanAmount,

        @DecimalMin(value = "0.01", message = "Maximum loan amount must be greater than zero")
        @Digits(integer = 13, fraction = 2, message = "Maximum loan amount must have at most 13 integer digits and 2 decimal places")
        BigDecimal maxLoanAmount,

        InterestType interestType,

        @DecimalMin(value = "0.0000", message = "Minimum interest rate cannot be negative")
        @Digits(integer = 3, fraction = 4, message = "Minimum interest rate must have at most 3 integer digits and 4 decimal places")
        BigDecimal minInterestRate,

        @DecimalMin(value = "0.0000", message = "Maximum interest rate cannot be negative")
        @Digits(integer = 3, fraction = 4, message = "Maximum interest rate must have at most 3 integer digits and 4 decimal places")
        BigDecimal maxInterestRate,

        DisbursementType disbursementType,

        @Min(value = 18, message = "Minimum applicant age must be at least 18")
        @Max(value = 100, message = "Minimum applicant age must not exceed 100")
        Integer applierMinAge,

        @Min(value = 18, message = "Maximum applicant age must be at least 18")
        @Max(value = 100, message = "Maximum applicant age must not exceed 100")
        Integer applierMaxAge,

        Boolean coBorrowerRequired,

        @Min(value = 300, message = "Minimum credit score must be at least 300")
        @Max(value = 900, message = "Minimum credit score must not exceed 900")
        Integer minCreditScore,

        // Null keeps the existing documents, a non-null set replaces them completely
        Set<UUID> requiredDocumentIds,

        // Null keeps the existing professions, a non-null set replaces them completely
        Set<UUID> eligibleProfessionIds,

        @Min(value = 1, message = "Minimum repayment tenure must be at least 1 year")
        Integer minTenureForRepayment,

        @Min(value = 1, message = "Maximum repayment tenure must be at least 1 year")
        Integer maxTenureForRepayment,

        Boolean prepaymentAllowed,

        @DecimalMin(value = "0.00", message = "Foreclosure charges cannot be negative")
        @Digits(integer = 8, fraction = 2, message = "Foreclosure charges must have at most 8 integer digits and 2 decimal places")
        BigDecimal foreclosureCharges,

        Boolean coursePeriodIncluded,

        @Min(value = 0, message = "Additional moratorium months cannot be negative")
        @Max(value = 120, message = "Additional moratorium months must not exceed 120")
        Integer additionalMonths,

        SchemeStatus status
) {
}