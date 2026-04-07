package com.dts.tasktracker.service;

import com.dts.tasktracker.dto.TaskCreateRequest;
import com.dts.tasktracker.dto.TaskResponse;
import com.dts.tasktracker.dto.TaskStatusUpdateRequest;
import com.dts.tasktracker.entity.Task;
import com.dts.tasktracker.entity.TaskStatus;
import com.dts.tasktracker.exception.TaskNotFoundException;
import com.dts.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void create_persistsAndReturnsResponse() {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("  Follow up  ");
        req.setDescription(" notes ");
        req.setStatus(TaskStatus.IN_PROGRESS);
        req.setDueDateTime(LocalDateTime.of(2026, 4, 8, 14, 0));

        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(7L);
            return t;
        });

        TaskResponse out = taskService.create(req);

        assertThat(out.getId()).isEqualTo(7L);
        assertThat(out.getTitle()).isEqualTo("Follow up");
        assertThat(out.getDescription()).isEqualTo(" notes ");
        assertThat(out.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(out.getDueDateTime()).isEqualTo(req.getDueDateTime());

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Follow up");
    }

    @Test
    void create_blankDescriptionBecomesNull() {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("T");
        req.setDescription("   ");
        req.setStatus(TaskStatus.TO_DO);
        req.setDueDateTime(LocalDateTime.now());

        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        TaskResponse out = taskService.create(req);
        assertThat(out.getDescription()).isNull();
    }

    @Test
    void findById_throwsWhenMissing() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void findById_returnsMappedResponse() {
        Task t = new Task();
        t.setId(2L);
        t.setTitle("A");
        t.setDescription(null);
        t.setStatus(TaskStatus.DONE);
        t.setDueDateTime(LocalDateTime.of(2026, 1, 1, 12, 0));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(t));

        TaskResponse out = taskService.findById(2L);
        assertThat(out.getTitle()).isEqualTo("A");
        assertThat(out.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void findAll_returnsAllMapped() {
        Task t = new Task();
        t.setId(1L);
        t.setTitle("X");
        t.setStatus(TaskStatus.TO_DO);
        t.setDueDateTime(LocalDateTime.now());
        when(taskRepository.findAll()).thenReturn(List.of(t));

        List<TaskResponse> all = taskService.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getTitle()).isEqualTo("X");
    }

    @Test
    void updateStatus_changesStatus() {
        Task existing = new Task();
        existing.setId(3L);
        existing.setTitle("T");
        existing.setStatus(TaskStatus.TO_DO);
        existing.setDueDateTime(LocalDateTime.now());
        when(taskRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskStatusUpdateRequest req = new TaskStatusUpdateRequest();
        req.setStatus(TaskStatus.DONE);

        TaskResponse out = taskService.updateStatus(3L, req);
        assertThat(out.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void delete_throwsWhenMissing() {
        when(taskRepository.existsById(5L)).thenReturn(false);
        assertThatThrownBy(() -> taskService.delete(5L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void delete_removesWhenPresent() {
        when(taskRepository.existsById(5L)).thenReturn(true);
        taskService.delete(5L);
        verify(taskRepository).deleteById(5L);
    }
}
