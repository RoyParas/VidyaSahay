package com.vidyasahay.vidyasahay.dto.request;

import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.request.AddressRequest;
import com.vidyasahay.vidyasahay.enums.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.DecimalMin;

public record UpdateStudentProfileRequest(
        @NotNull UUID instituteId,
        @NotNull UUID courseId,
        @NotNull UUID categoryId,
        @NotNull @Valid AddressRequest address,
        String location,
        @NotNull @Min(100000) @Max(999999) Integer pincode,
        @NotNull Gender gender,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank String fatherName,
        @NotBlank String motherName,
        @NotNull @DecimalMin("0.00") BigDecimal feesPaid,
        @NotNull @DecimalMin("0.00") BigDecimal feesPending,
        @NotNull @DecimalMin("0.00") BigDecimal annualFamilyIncome
) {}
