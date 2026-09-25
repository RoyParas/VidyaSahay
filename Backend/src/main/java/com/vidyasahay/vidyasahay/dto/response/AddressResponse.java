package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String country,
        String state,
        String district,
        String city
) {}
