package com.moduleoverloader.tasktracker.service;

import com.moduleoverloader.tasktracker.dto.CreateTaskRequest;
import com.moduleoverloader.tasktracker.dto.TaskResponse;
import com.moduleoverloader.tasktracker.entity.Task;
import com.moduleoverloader.tasktracker.enums.TaskStatus;
import com.moduleoverloader.tasktracker.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAllByOrderByIdAsc().stream()
                .map(TaskResponse::fromTaskEntity)
                .toList();
    }

    public Optional<TaskResponse> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(TaskResponse::fromTaskEntity);
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setDeadline(request.deadline());

        Task savedTask = taskRepository.save(task);

        return TaskResponse.fromTaskEntity(savedTask);
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Optional<TaskResponse> updateTaskStatus(Long id, TaskStatus status) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setStatus(status);
                    return taskRepository.save(task);
                })
                .map(TaskResponse::fromTaskEntity);
    }
}
