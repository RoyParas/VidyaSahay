package com.vidyasahay.vidyasahay.service;

import com.vidyasahay.vidyasahay.dto.request.UpdateStudentVerificationRequest;

import java.util.UUID;

public interface StudentVerificationService {

    void updateStatus(
            UUID studentId,
            UpdateStudentVerificationRequest request
    );
}
