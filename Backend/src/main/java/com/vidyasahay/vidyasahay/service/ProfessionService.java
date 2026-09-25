package com.vidyasahay.vidyasahay.service;

import java.util.List;
import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.request.ProfessionRequest;
import com.vidyasahay.vidyasahay.dto.response.ProfessionResponse;

public interface ProfessionService {

    List<ProfessionResponse> getAllProfessions();

    ProfessionResponse getProfessionById(UUID professionId);

    ProfessionResponse createProfession(
            ProfessionRequest request
    );

    ProfessionResponse updateProfession(
            UUID professionId,
            ProfessionRequest request
    );

    void deleteProfession(UUID professionId);
}