package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.CreateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.ScholarshipEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.UpdateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeSummaryResponseDTO;
import com.vidyasahay.vidyasahay.enums.PaymentFrequency;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.enums.ScholarshipAmountType;
import com.vidyasahay.vidyasahay.enums.ScholarshipType;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.service.ScholarshipSchemeService;

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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScholarshipSchemeController")
class ScholarshipSchemeControllerTest {

    @Mock
    private ScholarshipSchemeService scholarshipSchemeService;

    @InjectMocks
    private ScholarshipSchemeController scholarshipSchemeController;

    private ScholarshipSchemeSummaryResponseDTO summary(UUID schemeId) {
        return new ScholarshipSchemeSummaryResponseDTO(
                schemeId, "National Merit Scholarship", "MERIT_BASED", "2025-26", "ACTIVE");
    }

    @Test
    @DisplayName("GET /api/scholarship-scheme/all returns 200 with every scheme summary")
    void getAllScholarshipSchemes_success() {
        ScholarshipSchemeSummaryResponseDTO summary = summary(UUID.randomUUID());

        when(scholarshipSchemeService.getAllScholarshipSchemes()).thenReturn(List.of(summary));

        ResponseEntity<List<ScholarshipSchemeSummaryResponseDTO>> response =
                scholarshipSchemeController.getAllScholarshipSchemes();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(summary);
    }

    @Test
    @DisplayName("GET /api/scholarship-scheme/{id} returns 200 with the scheme detail")
    void getScholarshipSchemeDetails_success() {
        UUID schemeId = UUID.randomUUID();

        ScholarshipSchemeDetailedResponseDTO detail = new ScholarshipSchemeDetailedResponseDTO(
                schemeId, "National Merit Scholarship", "MERIT_BASED", "2025-26", "ACTIVE",
                LocalDate.now(), LocalDate.now().plusMonths(6),
                17, 30, 500_000L, 60.0,
                List.of("Aadhaar Card"), List.of("Engineering"), List.of("GEN"),
                40_000.0, "FIXED_AMOUNT", "YEARLY", 10_000_000.0);

        when(scholarshipSchemeService.getScholarshipSchemeById(schemeId)).thenReturn(detail);

        ResponseEntity<ScholarshipSchemeDetailedResponseDTO> response =
                scholarshipSchemeController.getScholarshipSchemeDetails(schemeId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(detail);
    }

    @Test
    @DisplayName("GET /api/scholarship-scheme/{id} propagates the not-found failure")
    void getScholarshipSchemeDetails_notFound() {
        UUID schemeId = UUID.randomUUID();

        when(scholarshipSchemeService.getScholarshipSchemeById(schemeId))
                .thenThrow(new ResourceNotFoundException("Scholarship scheme not found"));

        assertThatThrownBy(() ->
                scholarshipSchemeController.getScholarshipSchemeDetails(schemeId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("GET /api/scholarship-scheme/eligible returns 200 with the matching schemes")
    void getEligibleScholarshipSchemes_success() {
        ScholarshipEligibilityRequestDTO request = new ScholarshipEligibilityRequestDTO(
                new BigDecimal("300000"), new BigDecimal("78.50"));
        ScholarshipSchemeSummaryResponseDTO summary = summary(UUID.randomUUID());

        when(scholarshipSchemeService.getEligibleScholarshipSchemes(request))
                .thenReturn(List.of(summary));

        ResponseEntity<List<ScholarshipSchemeSummaryResponseDTO>> response =
                scholarshipSchemeController.getEligibleScholarshipSchemes(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(summary);
    }

    @Test
    @DisplayName("GET /api/scholarship-scheme/created-by-me returns 200 with the caller's schemes")
    void getScholarshipSchemesCreatedByMe_success() {
        ScholarshipSchemeSummaryResponseDTO summary = summary(UUID.randomUUID());

        when(scholarshipSchemeService.getScholarshipSchemesCreatedByMe())
                .thenReturn(List.of(summary));

        ResponseEntity<List<ScholarshipSchemeSummaryResponseDTO>> response =
                scholarshipSchemeController.getScholarshipSchemesCreatedByMe();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(summary);
    }

    @Test
    @DisplayName("POST /api/scholarship-scheme returns 201 with a Location header")
    void createScholarshipScheme_success() {
        UUID schemeId = UUID.randomUUID();

        CreateScholarshipSchemeRequestDTO request = new CreateScholarshipSchemeRequestDTO(
                "National Merit Scholarship", ScholarshipType.MERIT_BASED, "2025-26",
                LocalDate.now(), LocalDate.now().plusMonths(6),
                17, 30, 500_000L, 60.0,
                Set.of(), Set.of(), Set.of(),
                40_000.0, ScholarshipAmountType.FIXED_AMOUNT, PaymentFrequency.YEARLY, 10_000_000.0);

        when(scholarshipSchemeService.createScholarshipScheme(request)).thenReturn(schemeId);

        ResponseEntity<Void> response =
                scholarshipSchemeController.createScholarshipScheme(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation())
                .hasToString("/api/scholarship-scheme/" + schemeId);
    }

    @Test
    @DisplayName("PATCH /api/scholarship-scheme/{id} returns 200 and delegates both arguments")
    void updateScholarshipScheme_success() {
        UUID schemeId = UUID.randomUUID();

        UpdateScholarshipSchemeRequestDTO request = new UpdateScholarshipSchemeRequestDTO(
                "Renamed Scholarship", null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, SchemeStatus.INACTIVE);

        ResponseEntity<Void> response =
                scholarshipSchemeController.updateScholarshipScheme(schemeId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(scholarshipSchemeService).updateScholarshipScheme(schemeId, request);
    }
}
