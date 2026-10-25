package com.moduleoverloader.tasktracker.service;

import com.moduleoverloader.tasktracker.dto.CreateTaskRequest;
import com.moduleoverloader.tasktracker.dto.TaskResponse;
import com.moduleoverloader.tasktracker.entity.Task;
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
        return taskRepository.findAll().stream()
                .map(this::toTaskResponse)
                .toList();
    }

    public Optional<TaskResponse> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::toTaskResponse);
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setDeadline(request.deadline());

        Task savedTask = taskRepository.save(task);

        return this.toTaskResponse(savedTask);
    }

    private TaskResponse toTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadline()
        );
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
