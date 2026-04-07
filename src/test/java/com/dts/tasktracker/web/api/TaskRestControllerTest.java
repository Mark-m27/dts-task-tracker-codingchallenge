package com.dts.tasktracker.web.api;

import com.dts.tasktracker.dto.TaskCreateRequest;
import com.dts.tasktracker.dto.TaskResponse;
import com.dts.tasktracker.dto.TaskStatusUpdateRequest;
import com.dts.tasktracker.entity.TaskStatus;
import com.dts.tasktracker.exception.RestExceptionHandler;
import com.dts.tasktracker.exception.TaskNotFoundException;
import com.dts.tasktracker.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskRestController.class)
@Import(RestExceptionHandler.class)
class TaskRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void create_returns201AndBody() throws Exception {
        TaskResponse created = sampleResponse(1L);
        when(taskService.create(any(TaskCreateRequest.class))).thenReturn(created);

        TaskCreateRequest body = new TaskCreateRequest();
        body.setTitle("Call client");
        body.setDescription("Urgent");
        body.setStatus(TaskStatus.TO_DO);
        body.setDueDateTime(LocalDateTime.of(2026, 5, 1, 9, 30));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/api/tasks/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Call client"));
    }

    @Test
    void create_validationError_returns400() throws Exception {
        TaskCreateRequest body = new TaskCreateRequest();
        body.setTitle("");
        body.setStatus(TaskStatus.TO_DO);
        body.setDueDateTime(LocalDateTime.now());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void getById_returns200() throws Exception {
        when(taskService.findById(2L)).thenReturn(sampleResponse(2L));

        mockMvc.perform(get("/api/tasks/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(taskService.findById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_returns200() throws Exception {
        when(taskService.findAll()).thenReturn(List.of(sampleResponse(1L)));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void patchStatus_returns200() throws Exception {
        TaskResponse updated = sampleResponse(3L);
        updated.setStatus(TaskStatus.DONE);
        when(taskService.updateStatus(eq(3L), any(TaskStatusUpdateRequest.class))).thenReturn(updated);

        TaskStatusUpdateRequest body = new TaskStatusUpdateRequest();
        body.setStatus(TaskStatus.DONE);

        mockMvc.perform(patch("/api/tasks/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/tasks/4"))
                .andExpect(status().isNoContent());
        verify(taskService).delete(4L);
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new TaskNotFoundException(4L)).when(taskService).delete(4L);

        mockMvc.perform(delete("/api/tasks/4"))
                .andExpect(status().isNotFound());
    }

    private static TaskResponse sampleResponse(long id) {
        TaskResponse r = new TaskResponse();
        r.setId(id);
        r.setTitle("Call client");
        r.setDescription("Urgent");
        r.setStatus(TaskStatus.TO_DO);
        r.setDueDateTime(LocalDateTime.of(2026, 5, 1, 9, 30));
        return r;
    }
}
