package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.CreateDisbursementRequest;
import com.vidyasahay.vidyasahay.dto.response.DisbursementDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.DisbursementSummaryResponse;
import com.vidyasahay.vidyasahay.service.DisbursementService;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/disbursement")
@Tag(name = "Disbursement", description = "Loan and scholarship disbursement APIs")
public class DisbursementController {

	private final DisbursementService disbursementService;

	public DisbursementController(DisbursementService disbursementService) {
		this.disbursementService = disbursementService;
	}

	@Operation(summary = "Get disbursements for authenticated bank or government user")
	@GetMapping("/my")
	@PreAuthorize("hasAnyRole('BANK', 'GOVERNMENT')")
	public ResponseEntity<List<DisbursementSummaryResponse>> getMyDisbursements(
			@AuthenticationPrincipal CustomUserPrincipal principal) {
		return ResponseEntity.ok(disbursementService.getMyDisbursements(principal));
	}

	@Operation(summary = "Get disbursement by ID")
	@GetMapping("/{disbursementId}")
	@PreAuthorize("hasAnyRole('BANK', 'GOVERNMENT')")
	public ResponseEntity<DisbursementDetailResponse> getDisbursementById(@PathVariable UUID disbursementId,
			@AuthenticationPrincipal CustomUserPrincipal principal) {
		return ResponseEntity.ok(disbursementService.getDisbursementById(disbursementId, principal));
	}

	@Operation(summary = "Create a disbursement")
	@PostMapping
	@PreAuthorize("hasAnyRole('BANK', 'GOVERNMENT')")
	public ResponseEntity<DisbursementDetailResponse> createDisbursement(
			@Valid @RequestBody CreateDisbursementRequest request,

			@AuthenticationPrincipal CustomUserPrincipal principal) {
		DisbursementDetailResponse response = disbursementService.createDisbursement(request, principal);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}