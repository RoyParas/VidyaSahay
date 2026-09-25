package com.vidyasahay.vidyasahay.dto.request;


import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.SchemeType;

import jakarta.validation.constraints.NotNull;

public record GetDocumentListRequest(

        @NotNull(message = "Scheme ID is required")
        UUID schemeId,

        @NotNull(message = "Scheme type is required")
        SchemeType schemeType

) {
}