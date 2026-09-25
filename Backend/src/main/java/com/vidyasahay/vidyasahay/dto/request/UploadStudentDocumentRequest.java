package com.vidyasahay.vidyasahay.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record UploadStudentDocumentRequest(
        @NotNull UUID documentTypeId
) {}
