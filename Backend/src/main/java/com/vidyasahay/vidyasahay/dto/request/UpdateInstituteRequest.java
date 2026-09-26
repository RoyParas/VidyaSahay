package com.vidyasahay.vidyasahay.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Payload used by ADMIN to update an institute.
 * Carries the self-update fields (firstName, lastName, mobile) plus the institute fields.
 */
public record UpdateInstituteRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Mobile is required")
        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Mobile must be a valid 10 digit number")
        String mobile,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is not valid")
        String email,

        @NotBlank(message = "Institute name is required")
        String instituteName,

        @NotNull(message = "Address is required")
        UUID addressId,

        String location,

        @NotNull(message = "Pincode is required")
        @Min(value = 100000, message = "Pincode must be a 6 digit number")
        @Max(value = 999999, message = "Pincode must be a 6 digit number")
        Integer pincode,

        String bankName,

        String branchName,

        @Pattern(regexp = "^$|^[A-Z]{4}0[A-Z0-9]{6}$", message = "IFSC code is not valid")
        String ifscCode,

        @Pattern(regexp = "^$|^[0-9]{9,18}$", message = "Account number is not valid")
        String accountNumber,

        @NotNull(message = "Status is required")
        Boolean status
) {
    public UpdateInstituteRequest(
            String firstName,
            String lastName,
            String mobile,
            String email,
            String instituteName,
            UUID addressId,
            String location,
            Integer pincode,
            String bankName,
            String branchName,
            String ifscCode,
            String accountNumber) {
        this(firstName, lastName, mobile, email, instituteName, addressId, location,
                pincode, bankName, branchName, ifscCode, accountNumber, null);
    }
}
