package com.vidyasahay.vidyasahay.dto.request;
import jakarta.validation.constraints.Pattern;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateBankRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email")
        String email,

        @NotBlank(message = "Mobile number is required")
        @Pattern(
                regexp = "^[6-9][0-9]{9}$",
                message = "Mobile number must be a valid 10-digit Indian mobile number"
        )
        String mobile,

        @NotBlank(message = "Bank name is required")
        String bankName

) {
}