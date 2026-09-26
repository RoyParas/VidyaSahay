package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.response.AddressResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationSummaryResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentDetailedResponse;
import com.vidyasahay.vidyasahay.enums.ApplicationStatus;
import com.vidyasahay.vidyasahay.enums.ApplicationType;
import com.vidyasahay.vidyasahay.enums.Gender;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Small response-DTO builders shared by the controller tests. */
final class ControllerTestData {

    private ControllerTestData() {
    }

    static StudentDetailedResponse studentDetail(UUID studentId) {
        return new StudentDetailedResponse(
                studentId,
                UUID.randomUUID(),
                "Jane",
                "Doe",
                "jane@example.com",
                "9876543210",
                "Vidya Institute",
                UUID.randomUUID(),
                "B.Tech Computer Engineering",
                UUID.randomUUID(),
                "GEN",
                UUID.randomUUID(),
                new AddressResponse(UUID.randomUUID(), "India", "Maharashtra", "Mumbai Suburban", "Mumbai"),
                "Andheri",
                400053,
                "XXXXXXXX9012",
                Gender.FEMALE,
                LocalDate.of(2003, 5, 17),
                "John Doe",
                "Mary Doe",
                new BigDecimal("50000.00"),
                new BigDecimal("100000.00"),
                new BigDecimal("300000.00"),
                VerificationStatus.VERIFIED,
                true,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    static ApplicationSummaryResponse applicationSummary(UUID applicationId) {
        return new ApplicationSummaryResponse(
                applicationId,
                ApplicationType.LOAN,
                UUID.randomUUID(),
                "Vidya Education Loan",
                UUID.randomUUID(),
                "Jane",
                "Doe",
                ApplicationStatus.SUBMITTED,
                new BigDecimal("250000.00"));
    }

    static ApplicationDetailResponse applicationDetail(UUID applicationId) {
        return new ApplicationDetailResponse(
                applicationSummary(applicationId),
                studentDetail(UUID.randomUUID()),
                "Vidya Institute",
                List.of());
    }
}
