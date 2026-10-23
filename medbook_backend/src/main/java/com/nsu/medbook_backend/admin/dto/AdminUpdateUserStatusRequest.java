package com.nsu.medbook_backend.admin.dto;

import jakarta.validation.constraints.NotNull;

public record AdminUpdateUserStatusRequest(
        @NotNull(message = "Active flag is required")
        Boolean active
) {
}
