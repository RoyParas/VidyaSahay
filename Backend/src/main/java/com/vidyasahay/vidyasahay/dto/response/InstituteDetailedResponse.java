package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.response.AddressResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record InstituteDetailedResponse(
        UUID instituteId,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String mobile,
        @NotBlank boolean status,
        @NotBlank String instituteName,
        @NotNull @Valid AddressResponse address,
        String location,
        @NotNull Integer pincode,
        String bankName,
        String branchName,
        String ifscCode,
        String accountNumber
) {}
