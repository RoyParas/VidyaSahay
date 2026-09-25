package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.loanScheme.CreateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.LoanEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.UpdateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeSummaryResponseDTO;
import com.vidyasahay.vidyasahay.enums.DisbursementType;
import com.vidyasahay.vidyasahay.enums.InterestType;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.service.LoanSchemeService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanSchemeController")
class LoanSchemeControllerTest {

    @Mock
    private LoanSchemeService loanSchemeService;

    @InjectMocks
    private LoanSchemeController loanSchemeController;

    private LoanSchemeSummaryResponseDTO summary(UUID schemeId) {
        return new LoanSchemeSummaryResponseDTO(
                schemeId, UUID.randomUUID(), "Vidya Education Loan",
                InterestType.FLOATING, 50_000, 1_000_000, SchemeStatus.ACTIVE);
    }

    @Test
    @DisplayName("GET /api/loan-scheme/all returns 200 with every scheme summary")
    void getAllLoanSchemes_success() {
        LoanSchemeSummaryResponseDTO summary = summary(UUID.randomUUID());

        when(loanSchemeService.getAllLoanSchemes()).thenReturn(List.of(summary));

        ResponseEntity<List<LoanSchemeSummaryResponseDTO>> response =
                loanSchemeController.getAllLoanSchemes();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(summary);
    }

    @Test
    @DisplayName("GET /api/loan-scheme/all propagates the empty-portal failure")
    void getAllLoanSchemes_empty() {
        when(loanSchemeService.getAllLoanSchemes())
                .thenThrow(new ResourceNotFoundException("No loan schemes are available on the portal"));

        assertThatThrownBy(() -> loanSchemeController.getAllLoanSchemes())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("GET /api/loan-scheme/eligible returns 200 with the matching schemes")
    void getEligibleLoanSchemes_success() {
        LoanEligibilityRequestDTO request =
                new LoanEligibilityRequestDTO(new BigDecimal("400000"));
        LoanSchemeSummaryResponseDTO summary = summary(UUID.randomUUID());

        when(loanSchemeService.getEligibleLoanSchemes(request)).thenReturn(List.of(summary));

        ResponseEntity<List<LoanSchemeSummaryResponseDTO>> response =
                loanSchemeController.getEligibleScholarshipSchemes(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(summary);
    }

    @Test
    @DisplayName("GET /api/loan-scheme/created-by-me returns 200 with the caller's schemes")
    void getLoanSchemesCreatedByMe_success() {
        LoanSchemeSummaryResponseDTO summary = summary(UUID.randomUUID());

        when(loanSchemeService.getLoanSchemesCreatedByMe()).thenReturn(List.of(summary));

        ResponseEntity<List<LoanSchemeSummaryResponseDTO>> response =
                loanSchemeController.getLoanSchemesCreatedByMe();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(summary);
    }

    @Test
    @DisplayName("GET /api/loan-scheme/{id} returns 200 with the scheme detail")
    void getLoanSchemeById_success() {
        UUID schemeId = UUID.randomUUID();

        LoanSchemeDetailedResponseDTO detail = new LoanSchemeDetailedResponseDTO(
                schemeId, UUID.randomUUID(), "Vidya Education Loan", InterestType.FLOATING,
                new BigDecimal("50000"), new BigDecimal("1000000"), SchemeStatus.ACTIVE,
                LocalDate.now(), LocalDate.now().plusYears(1),
                new BigDecimal("8.5"), new BigDecimal("12.5"), DisbursementType.YEARLY,
                18, 35, true, 700, List.of("Aadhaar Card"), List.of("Engineering"),
                2, 10, true, new BigDecimal("2.5"), true, 6);

        when(loanSchemeService.getLoanSchemeById(schemeId)).thenReturn(detail);

        ResponseEntity<LoanSchemeDetailedResponseDTO> response =
                loanSchemeController.getLoanSchemeById(schemeId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(detail);
    }

    @Test
    @DisplayName("POST /api/loan-scheme returns 201 with a Location header pointing at the new scheme")
    void createLoanScheme_success() {
        UUID schemeId = UUID.randomUUID();

        CreateLoanSchemeRequestDTO request = new CreateLoanSchemeRequestDTO(
                "Vidya Education Loan", LocalDate.now(), LocalDate.now().plusYears(1),
                new BigDecimal("50000"), new BigDecimal("1000000"),
                InterestType.FIXED, null, new BigDecimal("11.0"), DisbursementType.YEARLY,
                18, 35, Boolean.FALSE, null, Set.of(), Set.of(),
                2, 10, Boolean.FALSE, null, Boolean.TRUE, 6);

        when(loanSchemeService.createLoanScheme(request)).thenReturn(schemeId);

        ResponseEntity<Void> response = loanSchemeController.createLoanScheme(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation())
                .hasToString("/api/loan-scheme/" + schemeId);
    }

    @Test
    @DisplayName("PATCH /api/loan-scheme/{id} returns 200 and delegates both arguments")
    void updateLoanScheme_success() {
        UUID schemeId = UUID.randomUUID();

        UpdateLoanSchemeRequestDTO request = new UpdateLoanSchemeRequestDTO(
                "Renamed Scheme", null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, SchemeStatus.INACTIVE);

        ResponseEntity<Void> response =
                loanSchemeController.updateLoanScheme(schemeId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(loanSchemeService).updateLoanScheme(schemeId, request);
    }

    @Test
    @DisplayName("PATCH /api/loan-scheme/{id} propagates an authorization failure")
    void updateLoanScheme_notTheCreator() {
        UUID schemeId = UUID.randomUUID();

        UpdateLoanSchemeRequestDTO request = new UpdateLoanSchemeRequestDTO(
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null);

        org.mockito.Mockito.doThrow(
                        new AccessDeniedException("You are not authorized to update this loan scheme"))
                .when(loanSchemeService).updateLoanScheme(schemeId, request);

        assertThatThrownBy(() -> loanSchemeController.updateLoanScheme(schemeId, request))
                .isInstanceOf(AccessDeniedException.class);
    }
}
