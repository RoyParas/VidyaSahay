package com.vidyasahay.vidyasahay.dto.response;

import java.util.List;

public record ApplicationDetailResponse(
        ApplicationSummaryResponse applicationSummary,
        ApplicationSubmissionDetails submissionDetails,
        StudentDetailedResponse student,
        String instituteName,
        List<StudentDocumentResponse> documents,
        List<ApplicationHistoryResponse> history
) {
    public ApplicationDetailResponse(ApplicationSummaryResponse applicationSummary, StudentDetailedResponse student,
            String instituteName, List<StudentDocumentResponse> documents) {
        this(applicationSummary, null, student, instituteName, documents, List.of());
    }

    public ApplicationDetailResponse(ApplicationSummaryResponse applicationSummary,
            ApplicationSubmissionDetails submissionDetails, StudentDetailedResponse student,
            String instituteName, List<StudentDocumentResponse> documents) {
        this(applicationSummary, submissionDetails, student, instituteName, documents, List.of());
    }
}
