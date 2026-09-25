package com.vidyasahay.vidyasahay.service;

import com.vidyasahay.vidyasahay.dto.request.CreateDisbursementRequest;
import com.vidyasahay.vidyasahay.dto.response.DisbursementDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.DisbursementSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface DisbursementService {

    List<DisbursementSummaryResponse> getMyDisbursements(
            CustomUserPrincipal principal
    );

    DisbursementDetailResponse getDisbursementById(
            UUID disbursementId,
            CustomUserPrincipal principal
    );

    DisbursementDetailResponse createDisbursement(
            CreateDisbursementRequest request,
            CustomUserPrincipal principal
    );
}