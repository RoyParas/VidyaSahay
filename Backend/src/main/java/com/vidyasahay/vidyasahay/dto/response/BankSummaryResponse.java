package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record BankSummaryResponse(
        UUID bankId,
        String bankName,
        String contactPersonFirstName,
        String contactPersonLastName,
        String email,
        String mobile,
        boolean active
) {}