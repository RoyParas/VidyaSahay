package com.vidyasahay.vidyasahay.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vidyasahay.vidyasahay.dto.request.GetDocumentListRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateDocumentStatusRequest;
import com.vidyasahay.vidyasahay.dto.response.DocumentListResponse;
import com.vidyasahay.vidyasahay.dto.response.DocumentTypeResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentDocumentResponse;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.DocumentService;
import com.vidyasahay.vidyasahay.service.StudentDocumentService;
import com.vidyasahay.vidyasahay.service.StudentDocumentService.DocumentFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

	private final StudentDocumentService studentDocumentService;
	private final DocumentService documentService;

	public DocumentController(StudentDocumentService studentDocumentService, DocumentService documentService) {
		this.studentDocumentService = studentDocumentService;
		this.documentService = documentService;
	}

	@GetMapping("/{documentId}")
	@PreAuthorize("hasAnyRole('STUDENT','GOVERNMENT','BANK')")
	public ResponseEntity<StudentDocumentResponse> getDocument(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable UUID documentId) {
		StudentDocumentResponse response = studentDocumentService.getDocument(documentId, principal.getUserId(), principal.getRole().name());
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{documentId}/status")
	@PreAuthorize("hasAnyRole('GOVERNMENT','BANK')")
	public ResponseEntity<StudentDocumentResponse> updateDocumentStatus(
		@AuthenticationPrincipal CustomUserPrincipal principal, 
		@PathVariable UUID documentId,
		@Valid @RequestBody UpdateDocumentStatusRequest request
	) {
		StudentDocumentResponse response = studentDocumentService.updateDocumentStatus(documentId, request, principal.getUserId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/list")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<List<DocumentListResponse>> getDocumentListById(@Valid @ModelAttribute GetDocumentListRequest request) {
		List<DocumentListResponse> response = documentService.getDocumentListById(request);
		return ResponseEntity.ok(response);
	}

    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('BANK','GOVERNMENT')")
    public ResponseEntity<List<DocumentTypeResponse>> getAllDocumentTypes() {
        return ResponseEntity.ok(documentService.getAllDocumentTypes());
    }
}
