package kz.kassen.task_manager.controller;

import kz.kassen.task_manager.DTOs.TaskDTO;
import kz.kassen.task_manager.DTOs.TaskStatsResponse;
import kz.kassen.task_manager.model.Task;
import kz.kassen.task_manager.model.TaskStatus;
import kz.kassen.task_manager.model.User;
import kz.kassen.task_manager.repository.UserRepository;
import kz.kassen.task_manager.service.TaskService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    @GetMapping()
    public ResponseEntity<List<TaskDTO>> getTasks(@RequestParam(required = false) TaskStatus status, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        if (status != null) {
            return ResponseEntity.ok(taskService.getTasksByStatus(user.getId(), status));
        }
        return ResponseEntity.ok(taskService.getTasks(user.getId()));
    }

    @GetMapping("/stats")
    public ResponseEntity<TaskStatsResponse> getTasksResponse(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(taskService.getTaskStats(user.getId()));
    }

    @PostMapping()
    public ResponseEntity<Task> createTask(@RequestBody Task task, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Task newTask = taskService.createTask(task, user.getId());
        return ResponseEntity.ok(newTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        Task updatedTask = taskService.updateTask(id, task, user.getId());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        taskService.deleteTask(id, user.getId());
        return ResponseEntity.ok("Задача успешно удалена");
    }
}
