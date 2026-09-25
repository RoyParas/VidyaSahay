package com.vidyasahay.vidyasahay.service;

import java.util.List;
import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.CreateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.ScholarshipEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.UpdateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeSummaryResponseDTO;

public interface ScholarshipSchemeService {

    List<ScholarshipSchemeSummaryResponseDTO> getAllScholarshipSchemes();
    
    ScholarshipSchemeDetailedResponseDTO  getScholarshipSchemeById(UUID scholarshipSchemeId);
    
    List<ScholarshipSchemeSummaryResponseDTO> getEligibleScholarshipSchemes(ScholarshipEligibilityRequestDTO request);
    
    List<ScholarshipSchemeSummaryResponseDTO> getScholarshipSchemesCreatedByMe();
    
    UUID createScholarshipScheme(CreateScholarshipSchemeRequestDTO request);
    
    void updateScholarshipScheme(UUID scholarshipSchemeId, UpdateScholarshipSchemeRequestDTO request);
}