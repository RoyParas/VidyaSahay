package com.vidyasahay.vidyasahay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectApplicationRequest(
        @NotBlank @Size(max=1000) String remark
) {}
