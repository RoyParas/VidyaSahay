package com.vidyasahay.vidyasahay.service;

import java.util.List;
import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.request.CompleteStudentProfileRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentSummaryResponse;

public interface StudentService {

    List<StudentSummaryResponse> getAllStudents();

    List<StudentSummaryResponse> getStudentsForInstitute(UUID instituteUserId);

    StudentDetailedResponse getStudentById(UUID studentId);

    void completeProfile(UUID userId, CompleteStudentProfileRequest request);
}