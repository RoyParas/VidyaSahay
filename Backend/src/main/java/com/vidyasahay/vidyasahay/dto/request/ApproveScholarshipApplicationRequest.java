package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ApproveScholarshipApplicationRequest(
        @NotNull @DecimalMin("1.00") BigDecimal approvedAmount,
        @Size(max = 1000) String remark
) {}
