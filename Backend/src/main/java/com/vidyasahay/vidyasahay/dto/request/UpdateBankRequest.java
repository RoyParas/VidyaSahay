// package com.vidyasahay.vidyasahay.dto;

// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;

// public record UpdateBankRequest(
//         @NotBlank String firstName,
//         @NotBlank String lastName,
//         @NotBlank @Email String email,
//         @NotBlank String mobile,
//         //@NotBlank @Size(min=8,max=72) String temporaryPassword
// ) {}

package com.vidyasahay.vidyasahay.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateBankRequest(

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

        @NotNull(message = "Status is required")
        Boolean status
) {

    public UpdateBankRequest(String firstName, String lastName, String email, String mobile) {
        this(firstName, lastName, email, mobile, null);
    }
}
