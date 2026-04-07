package com.dts.tasktracker.service;

import com.dts.tasktracker.dto.TaskCreateRequest;
import com.dts.tasktracker.dto.TaskResponse;
import com.dts.tasktracker.dto.TaskStatusUpdateRequest;
import com.dts.tasktracker.entity.Task;
import com.dts.tasktracker.entity.TaskStatus;
import com.dts.tasktracker.exception.TaskNotFoundException;
import com.dts.tasktracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public TaskResponse create(TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle().trim());
        task.setDescription(emptyToNull(request.getDescription()));
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TO_DO);
        task.setDueDateTime(request.getDueDateTime());
        return toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return taskRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return taskRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatusUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.setStatus(request.getStatus());
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(Long id, TaskCreateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.setTitle(request.getTitle().trim());
        task.setDescription(emptyToNull(request.getDescription()));
        task.setStatus(request.getStatus());
        task.setDueDateTime(request.getDueDateTime());
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse r = new TaskResponse();
        r.setId(task.getId());
        r.setTitle(task.getTitle());
        r.setDescription(task.getDescription());
        r.setStatus(task.getStatus());
        r.setDueDateTime(task.getDueDateTime());
        return r;
    }

    private static String emptyToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s;
    }
}
