package com.moduleoverloader.tasktracker.controller;

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

import static com.moduleoverloader.tasktracker.enums.TaskStatus.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
    }

    @Test
    void getTaskById_taskDoesNotExist_shouldReturn404() throws Exception {
        when(taskService.getTaskById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks_twoTasks_shouldReturnAllTasks() throws Exception {
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
    }

    @Test
    void getAllTasks_noTasks_shouldReturnEmptyList() throws Exception {
        when(taskService.getAllTasks())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createTask_validRequest_shouldReturnCreatedTask() throws Exception {
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
    }

    @Test
    void createTask_missingDescription_shouldReturnCreatedTask() throws Exception {
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
    }

    @Test
    void createTask_missingTitle_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "description": "New task description",
                      "status": "TODO",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_blankTitle_shouldReturnBadRequest() throws Exception {
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_missingStatus_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "New task description",
                      "deadline": "2026-10-01T09:00:00"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_missingDeadline_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "title": "New task",
                      "description": "New task description",
                      "status": "TODO"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTask_taskExists_shouldReturnNoContent() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_taskDoesNotExist_shouldReturnNotFound() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/1"))
                .andExpect(status().isNotFound());
    }
}
