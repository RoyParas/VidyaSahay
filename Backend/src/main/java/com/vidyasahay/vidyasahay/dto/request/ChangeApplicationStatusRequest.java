package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.ApplicationStatus;

public record ChangeApplicationStatusRequest(
        @NotNull ApplicationStatus status,
        @Size(max=1000) String remark,
        UUID assignedToUserId
) {}
