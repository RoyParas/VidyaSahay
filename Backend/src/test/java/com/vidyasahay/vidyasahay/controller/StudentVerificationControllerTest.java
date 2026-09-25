package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.UpdateStudentVerificationRequest;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.service.StudentVerificationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentVerificationController")
class StudentVerificationControllerTest {

    @Mock
    private StudentVerificationService studentVerificationService;

    @InjectMocks
    private StudentVerificationController studentVerificationController;

    @Test
    @DisplayName("PUT /api/studentVerification/{id}/status returns 200 for a VERIFIED decision")
    void updateStatus_verified() {
        UUID studentId = UUID.randomUUID();
        UpdateStudentVerificationRequest request =
                new UpdateStudentVerificationRequest(VerificationStatus.VERIFIED, "All good");

        ResponseEntity<Void> response =
                studentVerificationController.updateStatus(studentId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(studentVerificationService).updateStatus(studentId, request);
    }

    @Test
    @DisplayName("PUT /api/studentVerification/{id}/status returns 200 for a REJECTED decision")
    void updateStatus_rejected() {
        UUID studentId = UUID.randomUUID();
        UpdateStudentVerificationRequest request =
                new UpdateStudentVerificationRequest(VerificationStatus.REJECTED, "Aadhaar illegible");

        assertThat(studentVerificationController.updateStatus(studentId, request).getStatusCode())
                .isEqualTo(HttpStatus.OK);

        verify(studentVerificationService).updateStatus(studentId, request);
    }

    @Test
    @DisplayName("PUT /api/studentVerification/{id}/status propagates a PENDING rejection")
    void updateStatus_pending() {
        UUID studentId = UUID.randomUUID();
        UpdateStudentVerificationRequest request =
                new UpdateStudentVerificationRequest(VerificationStatus.PENDING, "still checking");

        doThrow(new BusinessException("Institute must select a final verification status"))
                .when(studentVerificationService).updateStatus(studentId, request);

        assertThatThrownBy(() ->
                studentVerificationController.updateStatus(studentId, request))
                .isInstanceOf(BusinessException.class);
    }
}
