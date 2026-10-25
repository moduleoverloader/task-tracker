package com.moduleoverloader.tasktracker.service;

import com.moduleoverloader.tasktracker.dto.CreateTaskRequest;
import com.moduleoverloader.tasktracker.dto.TaskResponse;
import com.moduleoverloader.tasktracker.entity.Task;
import com.moduleoverloader.tasktracker.enums.TaskStatus;
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
    void getAllTasks_mapsTasksToTaskResponses() {
        when(taskRepository.findAll()).thenReturn(List.of(
                new Task(1L, "First task", "First description", TODO, OCTOBER_2026),
                new Task(2L, "Second task", "Second description", TaskStatus.IN_PROGRESS, NOVEMBER_2026)
        ));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertEquals(List.of(
                new TaskResponse(1L, "First task", "First description", TODO, OCTOBER_2026),
                new TaskResponse(2L, "Second task", "Second description", TaskStatus.IN_PROGRESS, NOVEMBER_2026)
        ), responses);
    }

    @Test
    void getTaskById_mapsTaskToTaskResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(
                new Task(1L, "First task", "First description", TODO, OCTOBER_2026))
        );

        TaskResponse response = taskService.getTaskById(1L).orElseThrow();

        assertEquals(
                new TaskResponse(1L, "First task", "First description", TODO, OCTOBER_2026), response
        );
    }

    @Test
    void createTask_mapsRequestAndReturnsSavedTask() {
        CreateTaskRequest request = new CreateTaskRequest("New task", "New task description", COMPLETED, OCTOBER_2026);
        Task savedTask = new Task(1L, "New task", "New task description", COMPLETED, OCTOBER_2026);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponse response = taskService.createTask(request);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        Task taskToSave = taskCaptor.getValue();
        assertEquals("New task", taskToSave.getTitle());
        assertEquals("New task description", taskToSave.getDescription());
        assertEquals(COMPLETED, taskToSave.getStatus());
        assertEquals(OCTOBER_2026, taskToSave.getDeadline());
        assertEquals(new TaskResponse(1L, "New task", "New task description", COMPLETED, OCTOBER_2026), response);
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
}