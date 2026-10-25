package com.moduleoverloader.tasktracker.dto;

import com.moduleoverloader.tasktracker.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime deadline
) {
}
