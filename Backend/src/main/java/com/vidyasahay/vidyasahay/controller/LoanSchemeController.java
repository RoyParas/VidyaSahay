package com.vidyasahay.vidyasahay.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vidyasahay.vidyasahay.dto.request.loanScheme.CreateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.LoanEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.UpdateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeSummaryResponseDTO;
import com.vidyasahay.vidyasahay.service.LoanSchemeService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/loan-scheme")
public class LoanSchemeController {

    private final LoanSchemeService loanSchemeService;

    public LoanSchemeController(LoanSchemeService loanSchemeService) {
        this.loanSchemeService = loanSchemeService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public ResponseEntity<List<LoanSchemeSummaryResponseDTO>> getAllLoanSchemes() {

        List<LoanSchemeSummaryResponseDTO> response = loanSchemeService.getAllLoanSchemes();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LoanSchemeSummaryResponseDTO>> getActiveLoanSchemes() {
        return ResponseEntity.ok(loanSchemeService.getActiveLoanSchemes());
    }

    @PostMapping("/eligible")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LoanSchemeSummaryResponseDTO>>
    getEligibleScholarshipSchemes(@RequestBody LoanEligibilityRequestDTO request) {
        return ResponseEntity.ok(loanSchemeService.getEligibleLoanSchemes(request));
    }

    @GetMapping("/created-by-me")
    @PreAuthorize("hasRole('BANK')")
    public ResponseEntity<List<LoanSchemeSummaryResponseDTO>> getLoanSchemesCreatedByMe() {
        List<LoanSchemeSummaryResponseDTO> response = loanSchemeService.getLoanSchemesCreatedByMe();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{loanSchemeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BANK', 'STUDENT')")
    public ResponseEntity<LoanSchemeDetailedResponseDTO> getLoanSchemeById(@PathVariable @NotNull(message = "Loan scheme ID is required") UUID loanSchemeId) {
        LoanSchemeDetailedResponseDTO response = loanSchemeService.getLoanSchemeById(loanSchemeId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('BANK')")
    public ResponseEntity<Void> createLoanScheme(@Valid @RequestBody CreateLoanSchemeRequestDTO request) {

        UUID loanSchemeId = loanSchemeService.createLoanScheme(request);

        URI location = URI.create("/api/loan-scheme/" + loanSchemeId);

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{loanSchemeId}")
    @PreAuthorize("hasRole('BANK')")
    public ResponseEntity<Void> updateLoanScheme(
            @PathVariable @NotNull(message = "Loan scheme ID is required") UUID loanSchemeId,
            @Valid @RequestBody UpdateLoanSchemeRequestDTO request) {

        loanSchemeService.updateLoanScheme(loanSchemeId, request);

        return ResponseEntity.ok().build();
    }
}
