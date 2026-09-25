package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.UpdateStudentVerificationRequest;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.StudentVerification;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.repository.StudentVerificationRepository;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentVerificationServiceImpl")
class StudentVerificationServiceImplTest {

    @Mock
    private StudentVerificationRepository studentVerificationRepository;

    @InjectMocks
    private StudentVerificationServiceImpl studentVerificationService;

    @Test
    @DisplayName("marks a student as VERIFIED, trims the remark and stamps the verification time")
    void updateStatus_verified() {
        Student student = TestData.student();
        StudentVerification verification =
                TestData.verification(student, VerificationStatus.PENDING);
        verification.setVerifiedAt(null);

        when(studentVerificationRepository.findByStudentId(student.getId()))
                .thenReturn(Optional.of(verification));

        studentVerificationService.updateStatus(
                student.getId(),
                new UpdateStudentVerificationRequest(
                        VerificationStatus.VERIFIED, "   All documents match   "));

        assertThat(verification.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(verification.getRemark()).isEqualTo("All documents match");
        assertThat(verification.getVerifiedAt()).isNotNull();

        verify(studentVerificationRepository).save(verification);
    }

    @Test
    @DisplayName("marks a student as REJECTED")
    void updateStatus_rejected() {
        Student student = TestData.student();
        StudentVerification verification =
                TestData.verification(student, VerificationStatus.PENDING);

        when(studentVerificationRepository.findByStudentId(student.getId()))
                .thenReturn(Optional.of(verification));

        studentVerificationService.updateStatus(
                student.getId(),
                new UpdateStudentVerificationRequest(
                        VerificationStatus.REJECTED, "Aadhaar is illegible"));

        assertThat(verification.getStatus()).isEqualTo(VerificationStatus.REJECTED);

        verify(studentVerificationRepository).save(verification);
    }

    @Test
    @DisplayName("fails when the student has no verification record")
    void updateStatus_recordNotFound() {
        UUID studentId = UUID.randomUUID();

        when(studentVerificationRepository.findByStudentId(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentVerificationService.updateStatus(
                studentId,
                new UpdateStudentVerificationRequest(VerificationStatus.VERIFIED, "ok")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("verification record not found");

        verify(studentVerificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("refuses to store PENDING back as a final decision")
    void updateStatus_pendingIsNotAFinalDecision() {
        Student student = TestData.student();
        StudentVerification verification =
                TestData.verification(student, VerificationStatus.PENDING);

        when(studentVerificationRepository.findByStudentId(student.getId()))
                .thenReturn(Optional.of(verification));

        assertThatThrownBy(() -> studentVerificationService.updateStatus(
                student.getId(),
                new UpdateStudentVerificationRequest(VerificationStatus.PENDING, "still checking")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("final verification status");

        verify(studentVerificationRepository, never()).save(any());
    }
}
