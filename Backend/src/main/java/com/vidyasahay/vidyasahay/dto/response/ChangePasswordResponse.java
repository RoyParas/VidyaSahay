package com.vidyasahay.vidyasahay.dto.response;

public record ChangePasswordResponse(
        String message,
        boolean mustChangePassword
) {
}
