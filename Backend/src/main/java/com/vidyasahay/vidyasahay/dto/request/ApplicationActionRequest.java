package com.vidyasahay.vidyasahay.dto.request;

import java.util.UUID;

public record ApplicationActionRequest(
        UUID applicationId,
        String status,
        String remark,
        Double approvedAmount
) {
}