package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.ChangePasswordRequest;
import com.vidyasahay.vidyasahay.dto.response.ChangePasswordResponse;
import com.vidyasahay.vidyasahay.dto.request.LoginRequest;
import com.vidyasahay.vidyasahay.dto.response.LoginResponse;
import com.vidyasahay.vidyasahay.dto.request.StudentRegistrationRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentRegistrationResponse;
import com.vidyasahay.vidyasahay.service.AuthService;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/register")
	public ResponseEntity<StudentRegistrationResponse> register(@Valid @RequestBody StudentRegistrationRequest request) {
		StudentRegistrationResponse response = authService.registerStudent(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Change mandatory password", description = """
			Allows authenticated BANK and INSTITUTE users to change
			their temporary password when mustChangePassword is true.
			""")
	@PutMapping("/change-password")
	@PreAuthorize("hasAnyRole('BANK', 'INSTITUTE')")
	public ResponseEntity<ChangePasswordResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request,
			@AuthenticationPrincipal CustomUserPrincipal principal) {
		return ResponseEntity.ok(authService.changePassword(request, principal));
	}
}
