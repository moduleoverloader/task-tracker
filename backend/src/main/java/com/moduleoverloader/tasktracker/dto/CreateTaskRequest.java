package com.moduleoverloader.tasktracker.dto;

import com.moduleoverloader.tasktracker.enums.TaskStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskRequest(
    @NotBlank
    @Size(max = 50, message = "Title must be 50 characters or fewer")
    String title,
    @Size(max = 255, message = "Description must be 255 characters or fewer")
    String description,
    @NotNull
    TaskStatus status,
    @NotNull
    LocalDateTime deadline
) {
    @AssertTrue(message = "Deadline must be between 2000 and 2099")
    public boolean isDeadlineWithinAllowedRange() {
        if (deadline == null) {
            return true;
        }
        int year = deadline.getYear();
        return year >= 2000 && year < 2100;
    }
}
