package com.vidyasahay.vidyasahay.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.Gender;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record CompleteStudentProfileRequest(

        @NotNull(message = "Institute ID is required")
        UUID instituteId,

        @NotNull(message = "Course ID is required")
        UUID courseId,

        @NotNull(message = "Category ID is required")
        UUID categoryId,

        @NotNull(message = "Address ID is required")
        UUID addressId,

        String location,

        @NotNull(message = "Pincode is required")
        @Min(value = 100000, message = "Pincode must contain 6 digits")
        @Max(value = 999999, message = "Pincode must contain 6 digits")
        Integer pincode,

        @NotBlank(message = "Aadhaar number is required")
        @Pattern(
                regexp = "^[0-9]{12}$",
                message = "Aadhaar number must contain exactly 12 digits"
        )
        String aadharNumber,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotBlank(message = "Father name is required")
        String fatherName,

        @NotBlank(message = "Mother name is required")
        String motherName,

        @NotNull(message = "Annual family income is required")
        @DecimalMin(
                value = "0.00",
                message = "Annual family income cannot be negative"
        )
        BigDecimal annualFamilyIncome

) {
}