package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.response.AddressResponse;
import com.vidyasahay.vidyasahay.dto.response.CategoryResponse;
import com.vidyasahay.vidyasahay.dto.response.CourseSummaryResponse;
import com.vidyasahay.vidyasahay.enums.Gender;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentDetailedResponse(
        UUID studentId,
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String mobile,
        String institute,
        UUID instituteId,
        String course,
        UUID courseId,
        String category,
        UUID categoryId,
        AddressResponse address,
        String location,
        Integer pincode,
        String maskedAadharNumber,
        Gender gender,
        LocalDate dateOfBirth,
        String fatherName,
        String motherName,
        BigDecimal feesPaid,
        BigDecimal feesPending,
        BigDecimal annualFamilyIncome,
        VerificationStatus verificationStatus,
        boolean profileCompleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
