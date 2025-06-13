package kz.kassen.task_manager.repository;

import kz.kassen.task_manager.model.Task;
import kz.kassen.task_manager.model.TaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
    Long countByUserIdAndStatus(Long userId, TaskStatus status);
    Long countByUserId(Long userId);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    List<Task> findLatestTasksByUserId(Long userId, Pageable pageable);
}
