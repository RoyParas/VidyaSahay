package com.vidyasahay.vidyasahay.service;

import com.vidyasahay.vidyasahay.dto.request.ApplicationActionRequest;
import com.vidyasahay.vidyasahay.dto.request.ApplyApplicationRequest;
import com.vidyasahay.vidyasahay.dto.response.ApplicationDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationSummaryResponse;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {

    ApplicationDetailResponse getApplicationById(
            UUID applicationId
    );

    List<ApplicationSummaryResponse> getMyApplications(
            CustomUserPrincipal principal
    );

    ApplicationDetailResponse apply(
            ApplyApplicationRequest request,
            CustomUserPrincipal principal
    );

    ApplicationDetailResponse updateStatus(
            ApplicationActionRequest request,
            CustomUserPrincipal principal
    );
}