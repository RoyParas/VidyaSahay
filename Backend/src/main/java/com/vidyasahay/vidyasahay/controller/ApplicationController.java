package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.ApplicationActionRequest;
import com.vidyasahay.vidyasahay.dto.request.ApplyApplicationRequest;
import com.vidyasahay.vidyasahay.dto.response.ApplicationDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationSummaryResponse;
import com.vidyasahay.vidyasahay.service.ApplicationService;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/application")
@Tag(
        name = "Application",
        description = "Loan and scholarship application APIs"
)
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService
    ) {
        this.applicationService = applicationService;
    }

    @Operation(
            summary = "Get application details"
    )
    @GetMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'BANK', 'GOVERNMENT', 'INSTITUTE', 'ADMIN')")
    public ResponseEntity<ApplicationDetailResponse>
    getApplicationById(
            @PathVariable UUID applicationId
    ) {
        return ResponseEntity.ok(
                applicationService.getApplicationById(
                        applicationId
                )
        );
    }

    @Operation(
            summary = "Get authenticated student's applications"
    )
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'BANK', 'GOVERNMENT', 'INSTITUTE', 'ADMIN')")
    public ResponseEntity<List<ApplicationSummaryResponse>>
    getMyApplications(
            @AuthenticationPrincipal
            CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                applicationService.getMyApplications(
                        principal
                )
        );
    }

    @Operation(
            summary = "Apply for loan or scholarship"
    )
    @PostMapping(
            value = "/apply",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationDetailResponse> apply(
            @ModelAttribute ApplyApplicationRequest request,
            @AuthenticationPrincipal
            CustomUserPrincipal principal
    ) {
        ApplicationDetailResponse response =
                applicationService.apply(
                        request,
                        principal
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Update application status"
    )
    @PatchMapping("/status")
    @PreAuthorize("hasAnyRole('BANK', 'GOVERNMENT')")
    public ResponseEntity<ApplicationDetailResponse>
    updateStatus(
            @RequestBody
            ApplicationActionRequest request,
            @AuthenticationPrincipal
            CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                applicationService.updateStatus(
                        request,
                        principal
                )
        );
    }
}