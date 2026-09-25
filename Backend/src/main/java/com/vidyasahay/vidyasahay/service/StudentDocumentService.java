package com.vidyasahay.vidyasahay.service;

import java.util.UUID;

import org.springframework.core.io.Resource;

import com.vidyasahay.vidyasahay.dto.request.UpdateDocumentStatusRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentDocumentResponse;

public interface StudentDocumentService {

    /** Wrapper around the physical file that is streamed back to the caller. */
    record DocumentFile(Resource resource, String fileName, String contentType) {}

    /** STUDENT (own only), GOVERNMENT, BANK : metadata of an uploaded document. */
    StudentDocumentResponse getDocument(UUID documentId, UUID requesterUserId, String role);

    /** STUDENT (own only), GOVERNMENT, BANK : the physical file from local storage. */
    DocumentFile loadDocumentFile(UUID documentId, UUID requesterUserId, String role);

    /** GOVERNMENT, BANK : verify or reject an uploaded document. */
    StudentDocumentResponse updateDocumentStatus(
            UUID documentId, UpdateDocumentStatusRequest request, UUID verifierUserId);
}
