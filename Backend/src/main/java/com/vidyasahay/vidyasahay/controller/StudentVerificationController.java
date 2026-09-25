package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.UpdateStudentVerificationRequest;
import com.vidyasahay.vidyasahay.service.StudentVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/student-verification")
public class StudentVerificationController {

    private final StudentVerificationService studentVerificationService;

    public StudentVerificationController(
            StudentVerificationService studentVerificationService) {
        this.studentVerificationService = studentVerificationService;
    }

    @PutMapping("/{studentId}/status")
    @PreAuthorize("hasRole('INSTITUTE')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID studentId,
            @Valid @RequestBody UpdateStudentVerificationRequest statusDTO) {

        studentVerificationService.updateStatus(studentId, statusDTO);
        return ResponseEntity.ok().build();
    }
}
