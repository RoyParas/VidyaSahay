package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentRegistrationRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp="^[0-9]{10}$") String mobile,
        @NotBlank @Size(min=8,max=72) String password
) {}
