package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import java.util.Set;

public record RevertApplicationRequest(
        @NotBlank @Size(max=1000) String remark,
        Set<UUID> requiredDocumentTypeIds
) {}
