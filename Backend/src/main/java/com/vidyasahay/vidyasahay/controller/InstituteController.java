package com.vidyasahay.vidyasahay.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vidyasahay.vidyasahay.dto.request.CreateInstituteRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteContactRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteRequest;
import com.vidyasahay.vidyasahay.dto.response.InstituteAccountResponse;
import com.vidyasahay.vidyasahay.dto.response.InstituteDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentSummaryResponse;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.InstituteService;
import com.vidyasahay.vidyasahay.service.StudentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/institute")
public class InstituteController {

    private final InstituteService instituteService;
    private final StudentService studentService;

    public InstituteController(InstituteService instituteService, StudentService studentService) {
        this.instituteService = instituteService;
        this.studentService = studentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstituteDetailedResponse> createInstitute(@Valid @RequestBody CreateInstituteRequest request) {
        InstituteDetailedResponse response = instituteService.createInstitute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InstituteAccountResponse>> getAllInstitutes() {
        return ResponseEntity.ok(instituteService.getAllInstitutes());
    }

    @GetMapping("/{instituteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstituteDetailedResponse> getInstituteById(@PathVariable UUID instituteId) {
        return ResponseEntity.ok(instituteService.getInstituteById(instituteId));
    }

    @PatchMapping("/self")
    @PreAuthorize("hasRole('INSTITUTE')")
    public ResponseEntity<InstituteDetailedResponse> updateOwnInstitute(
        @AuthenticationPrincipal CustomUserPrincipal principal,
        @Valid @RequestBody UpdateInstituteContactRequest request
    ) {
        InstituteDetailedResponse response = instituteService.updateOwnInstitute(principal.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-students")
    @PreAuthorize("hasRole('INSTITUTE')")
    public ResponseEntity<List<StudentSummaryResponse>> getMyStudents(
        @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(studentService.getStudentsForInstitute(principal.getUserId()));
    }

    @PatchMapping("/{instituteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstituteDetailedResponse> updateInstitute(
        @PathVariable UUID instituteId,
        @Valid @RequestBody UpdateInstituteRequest request
    ) {
        return ResponseEntity.ok(instituteService.updateInstitute(instituteId, request));
    }
}
