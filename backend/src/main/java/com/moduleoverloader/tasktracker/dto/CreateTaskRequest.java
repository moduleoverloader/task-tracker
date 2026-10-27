package com.moduleoverloader.tasktracker.dto;

import com.moduleoverloader.tasktracker.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateTaskRequest(
    @NotBlank
    String title,
    String description,
    @NotNull
    TaskStatus status,
    @NotNull
    LocalDateTime deadline
) {}
