package com.moduleoverloader.tasktracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskRequest(
    @NotBlank(message = "Title is required and must not be blank")
    @Size(max = 50, message = "Title must be 50 characters or fewer")
    String title,
    @Size(max = 255, message = "Description must be 255 characters or fewer")
    String description,
    @NotNull(message = "Status is required")
    @Pattern(regexp = "TODO|IN_PROGRESS|COMPLETED",
            message = "Status must be TODO, IN_PROGRESS, or COMPLETED")
    String status,
    @NotNull(message = "Deadline is required")
    @DeadlineWithinAllowedRange
    LocalDateTime deadline
) {}
