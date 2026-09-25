package com.vidyasahay.vidyasahay.dto.response;

import com.vidyasahay.vidyasahay.dto.response.StudentDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentDocumentResponse;

import java.util.List;

public record ApplicationDetailResponse(
        ApplicationSummaryResponse applicationSummary,
        StudentDetailedResponse student,
        String instituteName,
        List<StudentDocumentResponse> documents
) {
}