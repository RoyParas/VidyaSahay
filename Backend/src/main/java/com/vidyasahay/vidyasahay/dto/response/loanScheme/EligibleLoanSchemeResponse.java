package com.vidyasahay.vidyasahay.dto.response.loanScheme;

import java.util.List;

import com.vidyasahay.vidyasahay.dto.response.EligibilityCriterionResponse;
import com.vidyasahay.vidyasahay.dto.response.LookupResponse;

public record EligibleLoanSchemeResponse(
        LoanSchemeSummaryResponseDTO scheme,
        boolean eligible,
        List<EligibilityCriterionResponse> criteria,
        List<LookupResponse> missingDocuments
) {}
