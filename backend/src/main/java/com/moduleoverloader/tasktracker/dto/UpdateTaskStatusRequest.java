package com.moduleoverloader.tasktracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateTaskStatusRequest(
        @NotNull(message = "Status is required")
        @Pattern(regexp = "TODO|IN_PROGRESS|COMPLETED",
                message = "Status must be TODO, IN_PROGRESS, or COMPLETED")
        String status
) {}
