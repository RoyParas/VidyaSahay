package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record InstituteAccountResponse(
        UUID instituteId,
        String instituteName,
        String state,
        String city,
        boolean status
) {}
