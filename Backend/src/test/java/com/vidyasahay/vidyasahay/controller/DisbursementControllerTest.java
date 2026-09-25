package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.CreateDisbursementRequest;
import com.vidyasahay.vidyasahay.dto.response.DisbursementDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.DisbursementSummaryResponse;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.DisbursementService;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DisbursementController")
class DisbursementControllerTest {

    @Mock
    private DisbursementService disbursementService;

    @InjectMocks
    private DisbursementController disbursementController;

    private DisbursementDetailResponse detail(UUID disbursementId) {
        return new DisbursementDetailResponse(
                disbursementId, UUID.randomUUID(), "LOAN", UUID.randomUUID(), "Jane", "Doe",
                UUID.randomUUID(), "Asha", "Rao",
                new BigDecimal("75000.00"), LocalDate.now(), "COMPLETED", "First tranche");
    }

    @Test
    @DisplayName("GET /api/disbursement/my returns 200 with the caller's disbursements")
    void getMyDisbursements_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.BANK);

        DisbursementSummaryResponse summary = new DisbursementSummaryResponse(
                UUID.randomUUID(), UUID.randomUUID(), "LOAN", UUID.randomUUID(), "Jane", "Doe",
                new BigDecimal("75000.00"), LocalDate.now(), "COMPLETED", "First tranche");

        when(disbursementService.getMyDisbursements(principal)).thenReturn(List.of(summary));

        ResponseEntity<List<DisbursementSummaryResponse>> response =
                disbursementController.getMyDisbursements(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(summary);

        verify(disbursementService).getMyDisbursements(principal);
    }

    @Test
    @DisplayName("GET /api/disbursement/my propagates a wrong-role failure")
    void getMyDisbursements_wrongRole() {
        CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);

        when(disbursementService.getMyDisbursements(principal))
                .thenThrow(new RuntimeException(
                        "Only bank and government users can access disbursements"));

        assertThatThrownBy(() -> disbursementController.getMyDisbursements(principal))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("GET /api/disbursement/{id} returns 200 with the disbursement detail")
    void getDisbursementById_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.GOVERNMENT);
        UUID disbursementId = UUID.randomUUID();
        DisbursementDetailResponse expected = detail(disbursementId);

        when(disbursementService.getDisbursementById(disbursementId, principal))
                .thenReturn(expected);

        ResponseEntity<DisbursementDetailResponse> response =
                disbursementController.getDisbursementById(disbursementId, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
    }

    @Test
    @DisplayName("GET /api/disbursement/{id} propagates the not-found failure")
    void getDisbursementById_notFound() {
        CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
        UUID disbursementId = UUID.randomUUID();

        when(disbursementService.getDisbursementById(disbursementId, principal))
                .thenThrow(new RuntimeException("Disbursement not found"));

        assertThatThrownBy(() ->
                disbursementController.getDisbursementById(disbursementId, principal))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("POST /api/disbursement returns 201 with the created disbursement")
    void createDisbursement_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.BANK);

        CreateDisbursementRequest request = new CreateDisbursementRequest(
                UUID.randomUUID(), new BigDecimal("75000.00"), LocalDate.now(),
                "COMPLETED", "First tranche");

        DisbursementDetailResponse expected = detail(UUID.randomUUID());

        when(disbursementService.createDisbursement(request, principal)).thenReturn(expected);

        ResponseEntity<DisbursementDetailResponse> response =
                disbursementController.createDisbursement(request, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(expected);

        verify(disbursementService).createDisbursement(request, principal);
    }

    @Test
    @DisplayName("POST /api/disbursement propagates an invalid-status failure")
    void createDisbursement_invalidStatus() {
        CustomUserPrincipal principal = TestData.principal(RoleName.BANK);

        CreateDisbursementRequest request = new CreateDisbursementRequest(
                UUID.randomUUID(), new BigDecimal("75000.00"), LocalDate.now(),
                "SETTLED", "typo");

        when(disbursementService.createDisbursement(request, principal))
                .thenThrow(new RuntimeException("Invalid disbursement status"));

        assertThatThrownBy(() ->
                disbursementController.createDisbursement(request, principal))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid disbursement status");
    }
}
