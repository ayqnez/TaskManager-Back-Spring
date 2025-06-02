package kz.kassen.task_manager.service;

import kz.kassen.task_manager.DTOs.TaskDTO;
import kz.kassen.task_manager.model.Task;
import kz.kassen.task_manager.model.TaskStatus;
import kz.kassen.task_manager.model.User;
import kz.kassen.task_manager.repository.TaskRepository;
import kz.kassen.task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public List<TaskDTO> getTasks(Long userId) {
        return taskRepository.findByUserId(userId)
                .stream()
                .map(task -> TaskDTO.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .createdAt(task.getCreatedAt())
                        .updatedAt(task.getUpdatedAt())
                        .build())
                .toList();
    }

    public List<TaskDTO> getTasksByStatus(Long userId, TaskStatus status) {
        return taskRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(task -> TaskDTO.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .createdAt(task.getCreatedAt())
                        .updatedAt(task.getUpdatedAt())
                        .build())
                .toList();
    }

    public Task createTask(Task task, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task task, Long userId) {
        Task taskToUpdate = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));

        if (!taskToUpdate.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setDescription(task.getDescription());
        taskToUpdate.setStatus(task.getStatus());

        return taskRepository.save(taskToUpdate);
    }

    public void deleteTask(Long taskId, Long userId) {
        Task taskToDelete = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        if (!taskToDelete.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        taskRepository.delete(taskToDelete);
    }
}
