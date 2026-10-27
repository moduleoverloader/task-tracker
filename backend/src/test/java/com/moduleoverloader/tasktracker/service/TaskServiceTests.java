package com.moduleoverloader.tasktracker.service;

import com.moduleoverloader.tasktracker.dto.CreateTaskRequest;
import com.moduleoverloader.tasktracker.dto.TaskResponse;
import com.moduleoverloader.tasktracker.entity.Task;
import com.moduleoverloader.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.moduleoverloader.tasktracker.enums.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private static final LocalDateTime OCTOBER_2026 = LocalDateTime.of(2026, 10, 1, 9, 0);
    private static final LocalDateTime NOVEMBER_2026 = LocalDateTime.of(2026, 11, 1, 9, 0);

    @Test
    void getAllTasks_tasksExist_mapsTasksToTaskResponses() {
        when(taskRepository.findAll()).thenReturn(List.of(
                new Task(1L, "First task", "First description", TODO, OCTOBER_2026),
                new Task(2L, "Second task", "Second description", IN_PROGRESS, NOVEMBER_2026)
        ));
        List<TaskResponse> expectedResponses = List.of(
                new TaskResponse(1L, "First task", "First description", TODO, OCTOBER_2026),
                new TaskResponse(2L, "Second task", "Second description", IN_PROGRESS, NOVEMBER_2026)
        );

        List<TaskResponse> actualResponses = taskService.getAllTasks();

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllTasks_tasksDoNotExist_returnsEmptyList() {
        when(taskRepository.findAll()).thenReturn(List.of());

        List<TaskResponse> actualResponses = taskService.getAllTasks();

        assertTrue(actualResponses.isEmpty());
    }

    @Test
    void getTaskById_taskExists_mapsTaskToTaskResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(
                new Task(1L, "First task", "First description", TODO, OCTOBER_2026))
        );

        TaskResponse actualResponse = taskService.getTaskById(1L).orElseThrow();

        TaskResponse expectedResponse = new TaskResponse(1L, "First task", "First description", TODO, OCTOBER_2026);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getTaskById_taskDoesNotExist_returnsEmptyOptional() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TaskResponse> actualResponse = taskService.getTaskById(1L);

        assertTrue(actualResponse.isEmpty());
    }

    @Test
    void createTask_mapsRequestAndReturnsSavedTask() {
        CreateTaskRequest request = new CreateTaskRequest("New task", "New task description", COMPLETED, OCTOBER_2026);
        Task task = new Task(1L, "New task", "New task description", COMPLETED, OCTOBER_2026);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse actualResponse = taskService.createTask(request);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        Task taskToSave = taskCaptor.getValue();
        assertEquals("New task", taskToSave.getTitle());
        assertEquals("New task description", taskToSave.getDescription());
        assertEquals(COMPLETED, taskToSave.getStatus());
        assertEquals(OCTOBER_2026, taskToSave.getDeadline());
        TaskResponse expectedResponse = new TaskResponse(1L, "New task", "New task description", COMPLETED, OCTOBER_2026);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void deleteTask_existingTask_returnsTrue() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        boolean result = taskService.deleteTask(1L);

        assertTrue(result);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_nonExistingTask_returnsFalse() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        boolean result = taskService.deleteTask(1L);

        assertFalse(result);
        verify(taskRepository, never()).deleteById(1L);
    }

    @Test
    void updateTaskStatus_existingTask_updatesStatusAndReturnsResponse() {
        Task task = new Task(1L, "Existing task", "Existing description", TODO, OCTOBER_2026);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Optional<TaskResponse> actualResponse = taskService.updateTaskStatus(1L, IN_PROGRESS);

        TaskResponse expectedResponse = new TaskResponse(1L, "Existing task", "Existing description", IN_PROGRESS, OCTOBER_2026);
        assertEquals(Optional.of(expectedResponse), actualResponse);
        assertEquals(IN_PROGRESS, task.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void updateTaskStatus_nonExistingTask_returnsEmptyOptional() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TaskResponse> actualResponse = taskService.updateTaskStatus(1L, IN_PROGRESS);

        assertTrue(actualResponse.isEmpty());
        verify(taskRepository, never()).save(any(Task.class));
    }
}