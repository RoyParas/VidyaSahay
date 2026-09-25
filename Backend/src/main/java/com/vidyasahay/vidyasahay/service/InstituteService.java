package com.vidyasahay.vidyasahay.service;

import java.util.List;
import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.request.CreateInstituteRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteContactRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteRequest;
import com.vidyasahay.vidyasahay.dto.response.InstituteAccountResponse;
import com.vidyasahay.vidyasahay.dto.response.InstituteDetailedResponse;

public interface InstituteService {

    /** ADMIN : creates the institute together with its login user. */
    InstituteDetailedResponse createInstitute(CreateInstituteRequest request);

    /** ADMIN : lists every institute on the portal. */
    List<InstituteAccountResponse> getAllInstitutes();

    /** ADMIN : one institute in detail. */
    InstituteDetailedResponse getInstituteById(UUID instituteId);

    /** ADMIN : updates institute and its contact user. */
    InstituteDetailedResponse updateInstitute(UUID instituteId, UpdateInstituteRequest request);

    /** INSTITUTE : updates own contact details, institute resolved from the token. */
    InstituteDetailedResponse updateOwnInstitute(UUID userId, UpdateInstituteContactRequest request);
}
