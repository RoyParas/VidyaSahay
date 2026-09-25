package com.vidyasahay.vidyasahay.dto.response.scholarshipScheme;

import java.util.UUID;

public record ScholarshipSchemeSummaryResponseDTO(
        UUID scholarshipSchemeId,
        String scholarshipName,
        String scholarshipType,
        String academicYear,
        String status) {
}