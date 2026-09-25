package com.vidyasahay.vidyasahay.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vidyasahay.vidyasahay.dto.request.ProfessionRequest;
import com.vidyasahay.vidyasahay.dto.response.ProfessionResponse;
import com.vidyasahay.vidyasahay.service.ProfessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/professions")
public class ProfessionController {

    private final ProfessionService professionService;

    public ProfessionController(
            ProfessionService professionService) {

        this.professionService = professionService;
    }

    /*
     * Get all professions
     *
     * GET /api/professions
     */
    @PreAuthorize("hasAnyRole('INSTITUTE','BANK','ADMIN','GOVERNMENT')")
    @GetMapping
    public ResponseEntity<List<ProfessionResponse>>
            getAllProfessions() {

        List<ProfessionResponse> professions =
                professionService.getAllProfessions();

        return ResponseEntity.ok(professions);
    }

    /*
     * Get profession by ID
     *
     * GET /api/professions/{professionId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{professionId}")
    public ResponseEntity<ProfessionResponse>
            getProfessionById(
                    @PathVariable UUID professionId) {

        ProfessionResponse profession =
                professionService.getProfessionById(
                        professionId
                );

        return ResponseEntity.ok(profession);
    }

    /*
     * Create profession
     *
     * POST /api/professions
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProfessionResponse>
            createProfession(
                    @Valid
                    @RequestBody ProfessionRequest request) {

        ProfessionResponse createdProfession =
                professionService.createProfession(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProfession);
    }

    /*
     * Partially update profession
     *
     * PATCH /api/professions/{professionId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{professionId}")
    public ResponseEntity<ProfessionResponse>
            updateProfession(
                    @PathVariable UUID professionId,
                    @Valid
                    @RequestBody ProfessionRequest request) {

        ProfessionResponse updatedProfession =
                professionService.updateProfession(
                        professionId,
                        request
                );

        return ResponseEntity.ok(updatedProfession);
    }

    /*
     * Delete profession
     *
     * DELETE /api/professions/{professionId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{professionId}")
    public ResponseEntity<Void> deleteProfession(
            @PathVariable UUID professionId) {

        professionService.deleteProfession(professionId);

        return ResponseEntity.noContent().build();
    }
}