package com.dts.tasktracker.web.mvc;

import com.dts.tasktracker.dto.TaskCreateRequest;
import com.dts.tasktracker.dto.TaskResponse;
import com.dts.tasktracker.dto.TaskStatusUpdateRequest;
import com.dts.tasktracker.entity.TaskStatus;
import com.dts.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tasks")
public class TaskWebController {

    private final TaskService taskService;

    public TaskWebController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks/list";
    }

    @GetMapping("/new")
    public String newTask(Model model) {
        TaskCreateRequest task = new TaskCreateRequest();
        task.setStatus(TaskStatus.TO_DO);
        model.addAttribute("task", task);
        model.addAttribute("isEdit", false);
        return "tasks/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("task") TaskCreateRequest task, BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "tasks/form";
        }
        taskService.create(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        TaskResponse existing = taskService.findById(id);
        TaskCreateRequest task = new TaskCreateRequest();
        task.setTitle(existing.getTitle());
        task.setDescription(existing.getDescription());
        task.setStatus(existing.getStatus());
        task.setDueDateTime(existing.getDueDateTime());
        model.addAttribute("task", task);
        model.addAttribute("isEdit", true);
        model.addAttribute("taskId", id);
        return "tasks/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("task") TaskCreateRequest task,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("taskId", id);
            return "tasks/form";
        }
        taskService.update(id, task);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest();
        request.setStatus(status);
        taskService.updateStatus(id, request);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        taskService.delete(id);
        return "redirect:/tasks";
    }
}
