package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.UpdateDocumentStatusRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentDocumentResponse;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.StudentDocumentService;
import com.vidyasahay.vidyasahay.service.StudentDocumentService.DocumentFile;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentDocumentController")
class StudentDocumentControllerTest {

    @Mock
    private StudentDocumentService studentDocumentService;

    @InjectMocks
    private DocumentController studentDocumentController;

    private StudentDocumentResponse documentResponse(UUID documentId, String status) {
        return new StudentDocumentResponse(
                documentId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Aadhaar Card",
                "Government issued identity proof",
                "aadhaar.pdf",
                "aadhaar.pdf",
                status,
                null,
                LocalDateTime.now());
    }

//     @Test
//     @DisplayName("GET /api/student-document/view/{id} streams the file inline with its content type")
//     void viewDocument_success() {
//         CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
//         UUID documentId = UUID.randomUUID();

//         Resource resource = new ByteArrayResource("pdf-bytes".getBytes(StandardCharsets.UTF_8));
//         DocumentFile file = new DocumentFile(resource, "aadhaar.pdf", "application/pdf");

//         when(studentDocumentService.loadDocumentFile(
//                 documentId, principal.getUserId(), RoleName.BANK.name()))
//                 .thenReturn(file);

//         ResponseEntity<Resource> response =
//                 studentDocumentController.viewDocument(principal, documentId);

//         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getBody()).isSameAs(resource);
//         assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
//         assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
//                 .contains("inline")
//                 .contains("aadhaar.pdf");

//         verify(studentDocumentService).loadDocumentFile(
//                 documentId, principal.getUserId(), RoleName.BANK.name());
//     }

//     @Test
//     @DisplayName("GET /api/student-document/view/{id} propagates an access denial")
//     void viewDocument_accessDenied() {
//         CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);
//         UUID documentId = UUID.randomUUID();

//         when(studentDocumentService.loadDocumentFile(
//                 documentId, principal.getUserId(), RoleName.STUDENT.name()))
//                 .thenThrow(new AccessDeniedException("You are not allowed to view this document"));

//         assertThatThrownBy(() ->
//                 studentDocumentController.viewDocument(principal, documentId))
//                 .isInstanceOf(AccessDeniedException.class);
//     }

    @Test
    @DisplayName("GET /api/student-document/{id} returns 200 with the document metadata")
    void getDocument_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.GOVERNMENT);
        UUID documentId = UUID.randomUUID();
        StudentDocumentResponse expected = documentResponse(documentId, "PENDING");

        when(studentDocumentService.getDocument(
                documentId, principal.getUserId(), RoleName.GOVERNMENT.name()))
                .thenReturn(expected);

        ResponseEntity<StudentDocumentResponse> response =
                studentDocumentController.getDocument(principal, documentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
    }

    @Test
    @DisplayName("GET /api/student-document/{id} propagates the not-found failure")
    void getDocument_notFound() {
        CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
        UUID documentId = UUID.randomUUID();

        when(studentDocumentService.getDocument(
                documentId, principal.getUserId(), RoleName.BANK.name()))
                .thenThrow(new ResourceNotFoundException("Document not found with id : " + documentId));

        assertThatThrownBy(() ->
                studentDocumentController.getDocument(principal, documentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("PATCH /api/student-document/{id}/status returns 200 with the verified document")
    void updateDocumentStatus_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
        UUID documentId = UUID.randomUUID();

        UpdateDocumentStatusRequest request =
                new UpdateDocumentStatusRequest(VerificationStatus.VERIFIED);
        StudentDocumentResponse expected = documentResponse(documentId, "VERIFIED");

        when(studentDocumentService.updateDocumentStatus(
                documentId, request, principal.getUserId()))
                .thenReturn(expected);

        ResponseEntity<StudentDocumentResponse> response =
                studentDocumentController.updateDocumentStatus(principal, documentId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);

        verify(studentDocumentService).updateDocumentStatus(
                documentId, request, principal.getUserId());
    }

    @Test
    @DisplayName("PATCH /api/student-document/{id}/status propagates a PENDING rejection")
    void updateDocumentStatus_pendingRejected() {
        CustomUserPrincipal principal = TestData.principal(RoleName.GOVERNMENT);
        UUID documentId = UUID.randomUUID();

        UpdateDocumentStatusRequest request =
                new UpdateDocumentStatusRequest(VerificationStatus.PENDING);

        when(studentDocumentService.updateDocumentStatus(
                documentId, request, principal.getUserId()))
                .thenThrow(new BusinessException(
                        "Verification status must be either VERIFIED or REJECTED"));

        assertThatThrownBy(() -> studentDocumentController.updateDocumentStatus(
                principal, documentId, request))
                .isInstanceOf(BusinessException.class);
    }
}
