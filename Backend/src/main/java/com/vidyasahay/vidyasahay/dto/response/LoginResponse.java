package com.vidyasahay.vidyasahay.dto.response;

import com.vidyasahay.vidyasahay.dto.response.AuthenticatedUserResponse;

public record LoginResponse(
        String accessToken,
        long expiresIn,
        AuthenticatedUserResponse user
) {}
