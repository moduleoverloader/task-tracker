package com.moduleoverloader.tasktracker.dto;

import com.moduleoverloader.tasktracker.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull
        TaskStatus status
) {}
