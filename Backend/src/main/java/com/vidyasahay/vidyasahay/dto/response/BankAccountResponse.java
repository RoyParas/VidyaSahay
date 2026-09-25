package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record BankAccountResponse(
        UUID userId,
        UUID bankId,
        String bankName,
        String email,
        String mobile,
        boolean status
) {}
