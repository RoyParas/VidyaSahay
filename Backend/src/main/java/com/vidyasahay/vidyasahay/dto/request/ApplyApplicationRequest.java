package com.vidyasahay.vidyasahay.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record ApplyApplicationRequest(
        String applicationType,
        UUID schemeId,
        MultipartFile[] documents,
        UUID[] documentTypeIds
) {
}