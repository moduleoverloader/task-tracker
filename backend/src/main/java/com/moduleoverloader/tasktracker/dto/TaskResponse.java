package com.moduleoverloader.tasktracker.dto;

import com.moduleoverloader.tasktracker.entity.Task;
import com.moduleoverloader.tasktracker.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime deadline
) {
    public static TaskResponse fromTaskEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadline()
        );
    }
}
