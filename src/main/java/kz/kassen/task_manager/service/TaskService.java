package kz.kassen.task_manager.service;

import kz.kassen.task_manager.DTOs.TaskDTO;
import kz.kassen.task_manager.DTOs.TaskStatsResponse;
import kz.kassen.task_manager.model.Task;
import kz.kassen.task_manager.model.TaskStatus;
import kz.kassen.task_manager.model.User;
import kz.kassen.task_manager.repository.TaskRepository;
import kz.kassen.task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    public TaskStatsResponse getTaskStats(Long userId) {
        long totalTasks = taskRepository.countByUserId(userId);
        if (totalTasks == 0) {
            return new TaskStatsResponse(0, 0, 0);
        }

        long done = taskRepository.countByUserIdAndStatus(userId, TaskStatus.done);
        long inProgress = taskRepository.countByUserIdAndStatus(userId, TaskStatus.in_progress);
        long todo = taskRepository.countByUserIdAndStatus(userId, TaskStatus.todo);

        return new TaskStatsResponse(
                calculatePercentage(done, totalTasks),
                calculatePercentage(inProgress, totalTasks),
                calculatePercentage(todo, totalTasks)
        );
    }

    public List<TaskDTO> getLatestTasks(Long userId, int limit) {
        return taskRepository.findLatestTasksByUserId(userId, PageRequest.of(0, limit))
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

    private int calculatePercentage(long count, long total) {
        return (int) Math.round((count * 100.0) / total);
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
