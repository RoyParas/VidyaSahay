package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record BankDetailedResponse(
        UUID userId,
        UUID bankId,
        String bankName,
        String contactPersonFirstName,
        String contactPersonLastName,
        String email,
        String mobile,
        boolean status
) {}