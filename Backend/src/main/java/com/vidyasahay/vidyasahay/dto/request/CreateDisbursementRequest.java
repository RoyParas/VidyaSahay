package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateDisbursementRequest(

		@NotNull(message = "Application ID is required") UUID applicationId,

		@NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,

		@NotNull(message = "Disbursement date is required") LocalDate disbursementDate,
		
		@NotNull(message = "Disbursement status is required") String status,

		@Size(max = 1000, message = "Remark cannot exceed 1000 characters") String remark) {
}