package com.vidyasahay.vidyasahay.service;

import com.vidyasahay.vidyasahay.dto.request.ChangePasswordRequest;
import com.vidyasahay.vidyasahay.dto.response.ChangePasswordResponse;
import com.vidyasahay.vidyasahay.dto.request.LoginRequest;
import com.vidyasahay.vidyasahay.dto.response.LoginResponse;
import com.vidyasahay.vidyasahay.dto.request.StudentRegistrationRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentRegistrationResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    StudentRegistrationResponse registerStudent(
            StudentRegistrationRequest request
    );

    ChangePasswordResponse changePassword(
        ChangePasswordRequest request,
        CustomUserPrincipal principal
    );
}