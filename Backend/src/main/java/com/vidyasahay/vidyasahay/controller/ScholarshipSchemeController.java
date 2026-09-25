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

import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.CreateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.ScholarshipEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.UpdateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeSummaryResponseDTO;
import com.vidyasahay.vidyasahay.service.ScholarshipSchemeService;

@RestController
@RequestMapping("/api/scholarship-scheme")
public class ScholarshipSchemeController {

    private final ScholarshipSchemeService scholarshipSchemeService;

    public ScholarshipSchemeController(ScholarshipSchemeService scholarshipSchemeService) {
        this.scholarshipSchemeService = scholarshipSchemeService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<List<ScholarshipSchemeSummaryResponseDTO>> getAllScholarshipSchemes() {

        List<ScholarshipSchemeSummaryResponseDTO> response = scholarshipSchemeService.getAllScholarshipSchemes();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{scholarshipSchemeId}")
    @PreAuthorize("hasAnyRole('ADMIN','GOVERNMENT','STUDENT')")
    public ResponseEntity<ScholarshipSchemeDetailedResponseDTO> getScholarshipSchemeDetails(@PathVariable UUID scholarshipSchemeId) {
        return ResponseEntity.ok(scholarshipSchemeService.getScholarshipSchemeById(scholarshipSchemeId));
    }
    
    @PostMapping("/eligible")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ScholarshipSchemeSummaryResponseDTO>>
    getEligibleScholarshipSchemes(@RequestBody ScholarshipEligibilityRequestDTO request) {
        return ResponseEntity.ok(scholarshipSchemeService.getEligibleScholarshipSchemes(request));
    }
    
    @GetMapping("/created-by-me")
    @PreAuthorize("hasRole('GOVERNMENT')")
    public ResponseEntity<List<ScholarshipSchemeSummaryResponseDTO>> getScholarshipSchemesCreatedByMe() {
        return ResponseEntity.ok(scholarshipSchemeService.getScholarshipSchemesCreatedByMe());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('GOVERNMENT')")
    public ResponseEntity<Void> createScholarshipScheme(@RequestBody CreateScholarshipSchemeRequestDTO request) {

        UUID schemeId = scholarshipSchemeService.createScholarshipScheme(request);

        URI location = URI.create("/api/scholarship-scheme/" + schemeId);
        return ResponseEntity.created(location).build();
    }
    
    @PatchMapping("/{scholarshipSchemeId}")
    @PreAuthorize("hasRole('GOVERNMENT')")
    public ResponseEntity<Void> updateScholarshipScheme(
            @PathVariable UUID scholarshipSchemeId,
            @RequestBody UpdateScholarshipSchemeRequestDTO request) {

        scholarshipSchemeService.updateScholarshipScheme(scholarshipSchemeId,request);

        return ResponseEntity.ok().build();
    }
}