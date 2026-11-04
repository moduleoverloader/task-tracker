package com.moduleoverloader.tasktracker.controller;

import com.moduleoverloader.tasktracker.dto.CreateTaskRequest;
import com.moduleoverloader.tasktracker.dto.TaskResponse;
import com.moduleoverloader.tasktracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.moduleoverloader.tasktracker.enums.TaskStatus.IN_PROGRESS;
import static com.moduleoverloader.tasktracker.enums.TaskStatus.TODO;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private static final LocalDateTime OCTOBER_2026 = LocalDateTime.of(2026, 10, 1, 9, 0);
    private static final LocalDateTime NOVEMBER_2026 = LocalDateTime.of(2026, 11, 1, 9, 0);

    @Test
    void getTaskById_taskExists_shouldReturnTask() throws Exception {
        TaskResponse response = new TaskResponse(1L, "First task", "First task description",  TODO, OCTOBER_2026);

        when(taskService.getTaskById(1L))
                .thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("First task"))
                .andExpect(jsonPath("$.description").value("First task description"))
                .andExpect(jsonPath("$.status").value(TODO.name()))
                .andExpect(jsonPath("$.deadline").value("2026-10-01T09:00:00"));
        verify(taskService).getTaskById(1L);
    }

    @Test
    void getTaskById_taskDoesNotExist_shouldReturn404() throws Exception {
        when(taskService.getTaskById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isNotFound());
        verify(taskService).getTaskById(1L);
    }

    @Test
    void getAllTasks_twoTasksExist_shouldReturnAllTasks() throws Exception {
        List<TaskResponse> response = List.of(
                new TaskResponse(1L, "First task", "First task description",  TODO, OCTOBER_2026),
                new TaskResponse(2L, "Second task", "Second task description",  IN_PROGRESS, NOVEMBER_2026)
        );

        when(taskService.getAllTasks())
                .thenReturn(response);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("First task"))
                .andExpect(jsonPath("$[0].description").value("First task description"))
                .andExpect(jsonPath("$[0].status").value(TODO.name()))
                .andExpect(jsonPath("$[0].deadline").value("2026-10-01T09:00:00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Second task"))
                .andExpect(jsonPath("$[1].description").value("Second task description"))
                .andExpect(jsonPath("$[1].status").value(IN_PROGRESS.name()))
                .andExpect(jsonPath("$[1].deadline").value("2026-11-01T09:00:00"));
        verify(taskService).getAllTasks();
    }

    @Test
    void getAllTasks_noTasksExist_shouldReturnEmptyListOfTasks() throws Exception {
        when(taskService.getAllTasks())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(taskService).getAllTasks();
    }

    @Test
    void createTask_validRequest_shouldCreateTaskAndReturnIt() throws Exception {
        TaskResponse response = new TaskResponse(1L, "New task", "New task description", TODO, OCTOBER_2026);

        when(taskService.createTask(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "New task description",
                      "status": "TODO",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New task"))
                .andExpect(jsonPath("$.description").value("New task description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.deadline").value("2026-10-01T09:00:00"));
        CreateTaskRequest expectedRequest = new CreateTaskRequest("New task", "New task description", "TODO", OCTOBER_2026);
        verify(taskService).createTask(expectedRequest);
    }

    @Test
    void createTask_missingDescription_shouldCreateTaskAndReturnIt() throws Exception {
        TaskResponse response = new TaskResponse(1L, "New task", null, TODO, OCTOBER_2026);

        when(taskService.createTask(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "status": "TODO",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New task"))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.deadline").value("2026-10-01T09:00:00"));
        CreateTaskRequest expectedRequest = new CreateTaskRequest("New task", null, "TODO", OCTOBER_2026);
        verify(taskService).createTask(expectedRequest);
    }

    @Test
    void createTask_descriptionLongerThan255Characters_shouldReturnBadRequestAndError() throws Exception {
        String longDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "%s",
                      "status": "TODO",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """.formatted(longDescription)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("description"))
                .andExpect(jsonPath("$.errors[0].message").value("Description must be 255 characters or fewer"));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_missingTitle_shouldReturnBadRequestAndError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "description": "New task description",
                      "status": "TODO",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("title"))
                .andExpect(jsonPath("$.errors[0].message").value("Title is required and must not be blank"));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_blankTitle_shouldReturnBadRequestAndError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "",
                      "description": "New task description",
                      "status": "TODO",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("title"))
                .andExpect(jsonPath("$.errors[0].message").value("Title is required and must not be blank"));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_titleLongerThan50Characters_shouldReturnBadRequestAndError() throws Exception {
        String longTitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "%s",
                      "description": "New task description",
                      "status": "TODO",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """.formatted(longTitle)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("title"))
                .andExpect(jsonPath("$.errors[0].message").value("Title must be 50 characters or fewer"));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_missingStatus_shouldReturnBadRequestAndError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "New task description",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("status"))
                .andExpect(jsonPath("$.errors[0].message").value("Status is required"));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_invalidStatus_shouldReturnBadRequestAndError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "New task description",
                      "status": "INVALID_STATUS",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("status"))
                .andExpect(jsonPath("$.errors[0].message").value("Status must be TODO, IN_PROGRESS, or COMPLETED"));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_invalidDeadline_shouldReturnBadRequestAndError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "New task description",
                      "status": "TODO",
                      "deadline": "invalid date"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"))
                .andExpect(jsonPath("$.errors[0].field").value("body"))
                .andExpect(jsonPath("$.errors[0].message").value("Request body is malformed or contains a value with an invalid format."));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_missingDeadline_shouldReturnBadRequestAndError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "New task description",
                      "status": "TODO"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("deadline"))
                .andExpect(jsonPath("$.errors[0].message").value("Deadline is required"));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_deadlineBefore2000_shouldReturnBadRequestAndError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "New task description",
                      "status": "TODO",
                      "deadline": "1999-12-31T23:59:59"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("deadline"))
                .andExpect(jsonPath("$.errors[0].message").value("Deadline must be between 2000 and 2099"));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_deadlineAfter2099_shouldReturnBadRequestAndError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "New task description",
                      "status": "TODO",
                      "deadline": "2100-01-01T00:00:00"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("deadline"))
                .andExpect(jsonPath("$.errors[0].message").value("Deadline must be between 2000 and 2099"));
        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_invalidTitleAndDescriptionAndDeadlineAndStatus_shouldReturnBadRequestAndErrors() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "",
                      "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
                      "status": "INVALID_STATUS",
                      "deadline": "2100-01-01T00:00:00"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors.length()").value(4))
                .andExpect(jsonPath("$.errors[*].field").value(containsInAnyOrder("title", "description", "deadline", "status")))
                .andExpect(jsonPath("$.errors[*].message").value(containsInAnyOrder(
                        "Title is required and must not be blank",
                        "Description must be 255 characters or fewer",
                        "Deadline must be between 2000 and 2099",
                        "Status must be TODO, IN_PROGRESS, or COMPLETED"
                )));
        verifyNoInteractions(taskService);
    }

    @Test
    void deleteTask_taskExists_shouldDeleteTaskAndReturnNoContent() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
        verify(taskService).deleteTask(1L);
    }

    @Test
    void deleteTask_taskDoesNotExist_shouldReturnNotFound() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/1"))
                .andExpect(status().isNotFound());
        verify(taskService).deleteTask(1L);
    }

    @Test
    void updateTaskStatus_taskExistsAndStatusValid_shouldUpdateTaskAndReturnIt() throws Exception {
        TaskResponse response = new TaskResponse(1L, "First task", "First task description", IN_PROGRESS, OCTOBER_2026);

        when(taskService.updateTaskStatus(1L, IN_PROGRESS))
                .thenReturn(Optional.of(response));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "status": "IN_PROGRESS"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("First task"))
                .andExpect(jsonPath("$.description").value("First task description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.deadline").value("2026-10-01T09:00:00"));
        verify(taskService).updateTaskStatus(1L, IN_PROGRESS);
    }

    @Test
    void updateTaskStatus_taskDoesNotExist_shouldReturnNotFound() throws Exception {
        when(taskService.updateTaskStatus(1L, IN_PROGRESS))
            .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "status": "IN_PROGRESS"
                    }
                    """))
                .andExpect(status().isNotFound());
        verify(taskService).updateTaskStatus(1L, IN_PROGRESS);
    }

    @Test
    void updateTaskStatus_missingStatus_shouldReturnBadRequestAndError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("status"))
                .andExpect(jsonPath("$.errors[0].message").value("Status is required"));
        verifyNoInteractions(taskService);
    }

    @Test
    void updateTaskStatus_invalidStatus_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "status": "INVALID"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("status"))
                .andExpect(jsonPath("$.errors[0].message").value(
                        "Status must be TODO, IN_PROGRESS, or COMPLETED"));
        verifyNoInteractions(taskService);
    }
}
