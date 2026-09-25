package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.dto.request.UpdateStudentVerificationRequest;
import com.vidyasahay.vidyasahay.service.StudentVerificationService;
import com.vidyasahay.vidyasahay.entity.StudentVerification;
import com.vidyasahay.vidyasahay.repository.StudentVerificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class StudentVerificationServiceImpl implements StudentVerificationService {

    private final StudentVerificationRepository studentVerificationRepository;

    public StudentVerificationServiceImpl(
            StudentVerificationRepository studentVerificationRepository) {
        this.studentVerificationRepository = studentVerificationRepository;
    }

    @Override
    @Transactional
    public void updateStatus(
            UUID studentId,
            UpdateStudentVerificationRequest request) {

        StudentVerification verification = studentVerificationRepository
                .findByStudentId(studentId)
                .orElseThrow(() -> new BusinessException(
                        "Student verification record not found for student ID: " + studentId));

        validateStatus(request.status());

        verification.setStatus(request.status());
        verification.setRemark(request.remark().trim());
        verification.setVerifiedAt(LocalDateTime.now());

        // Set verifiedBy here after resolving the logged-in User from SecurityContext.
        // The field is optional in your entity, so this API works without that mapping.

        studentVerificationRepository.save(verification);
    }

    private void validateStatus(VerificationStatus status) {
        if ("PENDING".equalsIgnoreCase(status.name())) {
            throw new BusinessException(
                    "Institute must select a final verification status");
        }
    }
}
