package com.vidyasahay.vidyasahay.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vidyasahay.vidyasahay.dto.request.CompleteStudentProfileRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentSummaryResponse;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.StudentService;

import jakarta.validation.Valid;
//  Institute Id 2c430498-8be9-4e24-87ae-669affbe070f 
@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/complete-profile")
    public ResponseEntity<Void> completeProfile(@AuthenticationPrincipal CustomUserPrincipal principal, @Valid @RequestBody CompleteStudentProfileRequest request) {
        studentService.completeProfile(principal.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<StudentSummaryResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTITUTE')")
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDetailedResponse> getStudentById(@PathVariable UUID studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }
}