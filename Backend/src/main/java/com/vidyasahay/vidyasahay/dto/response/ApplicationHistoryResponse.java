package com.vidyasahay.vidyasahay.dto.response;

import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationHistoryResponse(
        UUID historyId,
        ApplicationStatus status,
        String remark,
        UUID actionByUserId,
        String actionByName,
        UUID assignedToUserId,
        String assignedToName,
        LocalDateTime createdAt
) {}
