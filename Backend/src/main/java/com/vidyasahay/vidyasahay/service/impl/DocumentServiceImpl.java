package com.vidyasahay.vidyasahay.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidyasahay.vidyasahay.dto.request.GetDocumentListRequest;
import com.vidyasahay.vidyasahay.dto.response.DocumentListResponse;
import com.vidyasahay.vidyasahay.dto.response.DocumentTypeResponse;
import com.vidyasahay.vidyasahay.service.DocumentService;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeRequiredDocumentRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeRequiredDocumentRepository;
import com.vidyasahay.vidyasahay.repository.DocumentTypeRepository;


@Service
@Transactional(readOnly = true)
public class DocumentServiceImpl implements DocumentService {

    private final LoanSchemeRequiredDocumentRepository
            loanSchemeRequiredDocumentRepository;

    private final ScholarshipSchemeRequiredDocumentRepository
            scholarshipSchemeRequiredDocumentRepository;

    private final DocumentTypeRepository documentTypeRepository;
    
	public DocumentServiceImpl(LoanSchemeRequiredDocumentRepository loanSchemeRequiredDocumentRepository,
			ScholarshipSchemeRequiredDocumentRepository scholarshipSchemeRequiredDocumentRepository,
            DocumentTypeRepository documentTypeRepository) {
		this.loanSchemeRequiredDocumentRepository = loanSchemeRequiredDocumentRepository;
		this.scholarshipSchemeRequiredDocumentRepository = scholarshipSchemeRequiredDocumentRepository;
		this.documentTypeRepository = documentTypeRepository;
	}

    @Override
    public List<DocumentTypeResponse> getAllDocumentTypes() {
        return documentTypeRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(document -> document.getName().toLowerCase()))
                .map(document -> new DocumentTypeResponse(
                        document.getId(), document.getName(), document.getDescription()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentListResponse> getDocumentListById(
            GetDocumentListRequest request
    ) {

        return switch (request.schemeType()) {

            case LOAN -> loanSchemeRequiredDocumentRepository
                    .findByLoanSchemeIdOrderByDocumentTypeNameAsc(
                            request.schemeId()
                    )
                    .stream()
                    .map(requiredDocument -> new DocumentListResponse(
                            requiredDocument.getDocumentType().getId(),
                            requiredDocument.getDocumentType().getName()
                    ))
                    .toList();

            case SCHOLARSHIP -> scholarshipSchemeRequiredDocumentRepository
                    .findByScholarshipSchemeIdOrderByDocumentTypeNameAsc(
                            request.schemeId()
                    )
                    .stream()
                    .map(requiredDocument -> new DocumentListResponse(
                            requiredDocument.getDocumentType().getId(),
                            requiredDocument.getDocumentType().getName()
                    ))
                    .toList();
        };
    }

	
}
