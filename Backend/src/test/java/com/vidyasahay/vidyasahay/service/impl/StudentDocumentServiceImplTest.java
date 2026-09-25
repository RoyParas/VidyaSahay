package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.UpdateDocumentStatusRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentDocumentResponse;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.StudentDocument;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.StudentDocumentRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.service.StudentDocumentService.DocumentFile;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@code StudentDocumentServiceImpl} takes a plain {@code String} storage
 * directory in its constructor, so {@code @InjectMocks} cannot build it. It is
 * wired by hand against a JUnit {@code @TempDir} instead.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentDocumentServiceImpl")
class StudentDocumentServiceImplTest {

    @TempDir
    Path storageRoot;

    @Mock
    private StudentDocumentRepository studentDocumentRepository;

    @Mock
    private UserRepository userRepository;

    private StudentDocumentServiceImpl studentDocumentService;

    private Student student;

    @BeforeEach
    void setUp() {
        studentDocumentService = new StudentDocumentServiceImpl(
                studentDocumentRepository, userRepository, storageRoot.toString());

        student = TestData.student();
    }

    @Nested
    @DisplayName("getDocument")
    class GetDocument {

        @Test
        @DisplayName("returns the metadata for a bank reviewer")
        void getDocument_bank() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.PENDING);

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));

            StudentDocumentResponse response = studentDocumentService.getDocument(
                    document.getId(), UUID.randomUUID(), RoleName.BANK.name());

            assertThat(response.id()).isEqualTo(document.getId());
            assertThat(response.studentId()).isEqualTo(student.getId());
            assertThat(response.documentTypeName()).isEqualTo("Aadhaar Card");
            assertThat(response.verificationStatus()).isEqualTo("PENDING");
            assertThat(response.verifiedByUserId()).isNull();
        }

        @Test
        @DisplayName("lets a student read their own document")
        void getDocument_ownStudent() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.VERIFIED);

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));

            StudentDocumentResponse response = studentDocumentService.getDocument(
                    document.getId(), student.getUser().getId(), RoleName.STUDENT.name());

            assertThat(response.id()).isEqualTo(document.getId());
        }

        @Test
        @DisplayName("blocks a student from reading somebody else's document")
        void getDocument_otherStudent() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.VERIFIED);

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));

            assertThatThrownBy(() -> studentDocumentService.getDocument(
                    document.getId(), UUID.randomUUID(), RoleName.STUDENT.name()))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("not allowed");
        }

        @Test
        @DisplayName("fails for an unknown document id")
        void getDocument_notFound() {
            UUID documentId = UUID.randomUUID();

            when(studentDocumentRepository.findById(documentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentDocumentService.getDocument(
                    documentId, UUID.randomUUID(), RoleName.GOVERNMENT.name()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Document not found");
        }
    }

    @Nested
    @DisplayName("loadDocumentFile")
    class LoadDocumentFile {

        @Test
        @DisplayName("streams a file stored relative to the configured storage root")
        void loadDocumentFile_success() throws IOException {
            Files.writeString(storageRoot.resolve("aadhaar.pdf"), "pdf-bytes");

            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.PENDING);

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));

            DocumentFile file = studentDocumentService.loadDocumentFile(
                    document.getId(), UUID.randomUUID(), RoleName.BANK.name());

            assertThat(file.fileName()).isEqualTo("aadhaar.pdf");
            assertThat(file.resource().exists()).isTrue();
            assertThat(file.contentType()).isNotBlank();
        }

        @Test
        @DisplayName("fails when the row exists but the file is gone from disk")
        void loadDocumentFile_missingOnDisk() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.PENDING);

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));

            assertThatThrownBy(() -> studentDocumentService.loadDocumentFile(
                    document.getId(), UUID.randomUUID(), RoleName.BANK.name()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("missing on the server");
        }

        @Test
        @DisplayName("fails when the stored path is blank")
        void loadDocumentFile_blankPath() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.PENDING);
            document.setFilePath("   ");

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));

            assertThatThrownBy(() -> studentDocumentService.loadDocumentFile(
                    document.getId(), UUID.randomUUID(), RoleName.BANK.name()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("path is not available");
        }

        @Test
        @DisplayName("refuses a stored path that escapes the storage root")
        void loadDocumentFile_pathTraversal() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.PENDING);
            document.setFilePath("../../etc/passwd");

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));

            assertThatThrownBy(() -> studentDocumentService.loadDocumentFile(
                    document.getId(), UUID.randomUUID(), RoleName.BANK.name()))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Invalid document path");
        }

        @Test
        @DisplayName("applies the same ownership rule as getDocument")
        void loadDocumentFile_otherStudent() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.PENDING);

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));

            assertThatThrownBy(() -> studentDocumentService.loadDocumentFile(
                    document.getId(), UUID.randomUUID(), RoleName.STUDENT.name()))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("updateDocumentStatus")
    class UpdateDocumentStatus {

        @Test
        @DisplayName("moves a PENDING document to VERIFIED and records the verifier")
        void updateDocumentStatus_verified() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.PENDING);
            User verifier = TestData.user(RoleName.BANK);

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));
            when(userRepository.findById(verifier.getId())).thenReturn(Optional.of(verifier));
            when(studentDocumentRepository.save(any(StudentDocument.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            StudentDocumentResponse response = studentDocumentService.updateDocumentStatus(
                    document.getId(),
                    new UpdateDocumentStatusRequest(VerificationStatus.VERIFIED),
                    verifier.getId());

            assertThat(response.verificationStatus()).isEqualTo("VERIFIED");
            assertThat(response.verifiedByUserId()).isEqualTo(verifier.getId());
            assertThat(response.verifiedAt()).isNotNull();
            assertThat(document.getVerifiedBy()).isSameAs(verifier);
        }

        @Test
        @DisplayName("moves a PENDING document to REJECTED")
        void updateDocumentStatus_rejected() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.PENDING);
            User verifier = TestData.user(RoleName.GOVERNMENT);

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));
            when(userRepository.findById(verifier.getId())).thenReturn(Optional.of(verifier));
            when(studentDocumentRepository.save(any(StudentDocument.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            StudentDocumentResponse response = studentDocumentService.updateDocumentStatus(
                    document.getId(),
                    new UpdateDocumentStatusRequest(VerificationStatus.REJECTED),
                    verifier.getId());

            assertThat(response.verificationStatus()).isEqualTo("REJECTED");
        }

        @Test
        @DisplayName("refuses PENDING as a target status")
        void updateDocumentStatus_pendingNotAllowed() {
            assertThatThrownBy(() -> studentDocumentService.updateDocumentStatus(
                    UUID.randomUUID(),
                    new UpdateDocumentStatusRequest(VerificationStatus.PENDING),
                    UUID.randomUUID()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("VERIFIED or REJECTED");

            verify(studentDocumentRepository, never()).save(any());
        }

        @Test
        @DisplayName("refuses to re-apply the status the document already has")
        void updateDocumentStatus_alreadyInThatState() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.VERIFIED);

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));

            assertThatThrownBy(() -> studentDocumentService.updateDocumentStatus(
                    document.getId(),
                    new UpdateDocumentStatusRequest(VerificationStatus.VERIFIED),
                    UUID.randomUUID()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already marked");

            verify(studentDocumentRepository, never()).save(any());
        }

        @Test
        @DisplayName("fails for an unknown document id")
        void updateDocumentStatus_documentNotFound() {
            UUID documentId = UUID.randomUUID();

            when(studentDocumentRepository.findById(documentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentDocumentService.updateDocumentStatus(
                    documentId,
                    new UpdateDocumentStatusRequest(VerificationStatus.VERIFIED),
                    UUID.randomUUID()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("fails when the verifier account cannot be resolved")
        void updateDocumentStatus_verifierNotFound() {
            StudentDocument document =
                    TestData.studentDocument(student, VerificationStatus.PENDING);
            UUID verifierId = UUID.randomUUID();

            when(studentDocumentRepository.findById(document.getId()))
                    .thenReturn(Optional.of(document));
            when(userRepository.findById(verifierId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentDocumentService.updateDocumentStatus(
                    document.getId(),
                    new UpdateDocumentStatusRequest(VerificationStatus.VERIFIED),
                    verifierId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Logged in user not found");

            verify(studentDocumentRepository, never()).save(any());
        }
    }
}
