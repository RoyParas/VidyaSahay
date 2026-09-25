package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.ApplicationActionRequest;
import com.vidyasahay.vidyasahay.dto.request.ApplyApplicationRequest;
import com.vidyasahay.vidyasahay.dto.response.ApplicationDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationSummaryResponse;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.service.ApplicationService;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationController")
class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    @Test
    @DisplayName("GET /api/application/{id} returns 200 with the application detail")
    void getApplicationById_success() {
        UUID applicationId = UUID.randomUUID();
        ApplicationDetailResponse detail = ControllerTestData.applicationDetail(applicationId);

        when(applicationService.getApplicationById(applicationId)).thenReturn(detail);

        ResponseEntity<ApplicationDetailResponse> response =
                applicationController.getApplicationById(applicationId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(detail);
    }

    @Test
    @DisplayName("GET /api/application/{id} propagates the not-found failure")
    void getApplicationById_notFound() {
        UUID applicationId = UUID.randomUUID();

        when(applicationService.getApplicationById(applicationId))
                .thenThrow(new RuntimeException("Application not found"));

        assertThatThrownBy(() -> applicationController.getApplicationById(applicationId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Application not found");
    }

    @Test
    @DisplayName("GET /api/application/my returns 200 and passes the principal straight through")
    void getMyApplications_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);
        ApplicationSummaryResponse summary =
                ControllerTestData.applicationSummary(UUID.randomUUID());

        when(applicationService.getMyApplications(principal)).thenReturn(List.of(summary));

        ResponseEntity<List<ApplicationSummaryResponse>> response =
                applicationController.getMyApplications(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(summary);

        verify(applicationService).getMyApplications(principal);
    }

    @Test
    @DisplayName("GET /api/application/my returns 200 with an empty list when there is nothing to show")
    void getMyApplications_empty() {
        CustomUserPrincipal principal = TestData.principal(RoleName.BANK);

        when(applicationService.getMyApplications(principal)).thenReturn(List.of());

        assertThat(applicationController.getMyApplications(principal).getBody()).isEmpty();
    }

    @Test
    @DisplayName("POST /api/application/apply returns 201 with the created application")
    void apply_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);
        UUID applicationId = UUID.randomUUID();

        ApplyApplicationRequest request =
                new ApplyApplicationRequest("LOAN", UUID.randomUUID(), null, null);
        ApplicationDetailResponse detail = ControllerTestData.applicationDetail(applicationId);

        when(applicationService.apply(request, principal)).thenReturn(detail);

        ResponseEntity<ApplicationDetailResponse> response =
                applicationController.apply(request, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(detail);

        verify(applicationService).apply(request, principal);
    }

    @Test
    @DisplayName("POST /api/application/apply propagates a missing-profile failure")
    void apply_noStudentProfile() {
        CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);
        ApplyApplicationRequest request =
                new ApplyApplicationRequest("LOAN", UUID.randomUUID(), null, null);

        when(applicationService.apply(request, principal))
                .thenThrow(new RuntimeException("Student profile not found"));

        assertThatThrownBy(() -> applicationController.apply(request, principal))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student profile not found");
    }

    @Test
    @DisplayName("PATCH /api/application/status returns 200 with the updated application")
    void updateStatus_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
        UUID applicationId = UUID.randomUUID();

        ApplicationActionRequest request = new ApplicationActionRequest(
                applicationId, "APPROVED", "Sanctioned", 450_000.0);
        ApplicationDetailResponse detail = ControllerTestData.applicationDetail(applicationId);

        when(applicationService.updateStatus(request, principal)).thenReturn(detail);

        ResponseEntity<ApplicationDetailResponse> response =
                applicationController.updateStatus(request, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(detail);

        verify(applicationService).updateStatus(request, principal);
    }

    @Test
    @DisplayName("PATCH /api/application/status propagates an unknown-status failure")
    void updateStatus_invalidStatus() {
        CustomUserPrincipal principal = TestData.principal(RoleName.GOVERNMENT);
        ApplicationActionRequest request = new ApplicationActionRequest(
                UUID.randomUUID(), "SANCTIONED", "typo", null);

        when(applicationService.updateStatus(request, principal))
                .thenThrow(new IllegalArgumentException(
                        "No enum constant ApplicationStatus.SANCTIONED"));

        assertThatThrownBy(() -> applicationController.updateStatus(request, principal))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
