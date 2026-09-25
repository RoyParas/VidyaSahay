package com.vidyasahay.vidyasahay.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidyasahay.vidyasahay.dto.request.UpdateDocumentStatusRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentDocumentResponse;
import com.vidyasahay.vidyasahay.entity.StudentDocument;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.StudentDocumentRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.service.StudentDocumentService;

@Service
public class StudentDocumentServiceImpl implements StudentDocumentService {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final StudentDocumentRepository studentDocumentRepository;
    private final UserRepository userRepository;

    /** Root folder on the local disk where uploaded student documents live. */
    private final Path storageRoot;

    public StudentDocumentServiceImpl(
            StudentDocumentRepository studentDocumentRepository,
            UserRepository userRepository,
            @Value("${app.document.storage-dir:uploads/student-documents}") String storageDir) {

        this.studentDocumentRepository = studentDocumentRepository;
        this.userRepository = userRepository;
        this.storageRoot = Paths.get(storageDir).toAbsolutePath().normalize();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDocumentResponse getDocument(
            UUID documentId, UUID requesterUserId, String role) {

        StudentDocument document = findDocument(documentId);
        checkAccess(document, requesterUserId, role);

        return mapToResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentFile loadDocumentFile(
            UUID documentId, UUID requesterUserId, String role) {

        StudentDocument document = findDocument(documentId);
        checkAccess(document, requesterUserId, role);

        Path filePath = resolveFilePath(document.getFilePath());

        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new ResourceNotFoundException(
                    "Document file is missing on the server : " + document.getFileName());
        }

        Resource resource;

        try {
            resource = new UrlResource(filePath.toUri());
        } catch (IOException exception) {
            throw new BusinessException("Unable to read the document file");
        }

        String contentType;

        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException exception) {
            contentType = null;
        }

        if (contentType == null || contentType.isBlank()) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        return new DocumentFile(resource, document.getFileName(), contentType);
    }

    @Override
    @Transactional
    public StudentDocumentResponse updateDocumentStatus(
            UUID documentId,
            UpdateDocumentStatusRequest request,
            UUID verifierUserId) {

        VerificationStatus status = request.verificationStatus();

        // Only VERIFIED and REJECTED are accepted from this endpoint
        if (status != VerificationStatus.VERIFIED
                && status != VerificationStatus.REJECTED) {
            throw new BusinessException(
                    "Verification status must be either VERIFIED or REJECTED");
        }

        StudentDocument document = findDocument(documentId);

        VerificationStatus currentStatus = document.getVerificationStatus();

        // Do not allow the same status to be applied again
        if (currentStatus == status) {
            throw new BusinessException(
                    "Document is already marked as " + status.name());
        }

        // Once verified, the document status cannot be changed
        if (currentStatus == VerificationStatus.VERIFIED) {
            throw new BusinessException(
                    "Verified document status cannot be changed");
        }

        User verifier = userRepository.findById(verifierUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));

        document.setVerificationStatus(status);
        document.setVerifiedBy(verifier);
        document.setVerifiedAt(LocalDateTime.now());

        StudentDocument saved = studentDocumentRepository.save(document);

        return mapToResponse(saved);
    }

    // ---------------------------------------------------------------- helpers

    private StudentDocument findDocument(UUID documentId) {
        return studentDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found with id : " + documentId));
    }

    /**
     * A student may only open their own documents, government and bank may open
     * any.
     */
    private void checkAccess(StudentDocument document, UUID requesterUserId, String role) {

        if (RoleName.STUDENT.name().equals(role)) {

            UUID ownerUserId = document.getStudent().getUser().getId();

            if (!ownerUserId.equals(requesterUserId)) {
                throw new AccessDeniedException("You are not allowed to view this document");
            }
        }
    }

    /**
     * filePath in the database may be stored either absolute or relative to the
     * storage root.
     * Relative paths are resolved against the root and checked so that nobody can
     * escape it.
     */
    private Path resolveFilePath(String storedPath) {

        if (storedPath == null || storedPath.isBlank()) {
            throw new ResourceNotFoundException("Document path is not available");
        }

        Path candidate = Paths.get(storedPath.trim());

        Path resolved = candidate.isAbsolute()
                ? candidate.normalize()
                : storageRoot.resolve(candidate).normalize();

        if (!resolved.startsWith(storageRoot)) {
            throw new AccessDeniedException("Invalid document path");
        }

        return resolved;
    }

    private StudentDocumentResponse mapToResponse(StudentDocument document) {

        return new StudentDocumentResponse(
                document.getId(),
                document.getStudent().getId(),
                document.getDocumentType().getId(),
                document.getDocumentType().getName(),
                document.getDocumentType()
                        .getDescription(),
                document.getFileName(),
                document.getFilePath(),
                document.getVerificationStatus()
                        .name(),
                document.getVerifiedBy() == null
                        ? null
                        : document.getVerifiedBy()
                                .getId(),
                document.getVerifiedAt());
    }
}
