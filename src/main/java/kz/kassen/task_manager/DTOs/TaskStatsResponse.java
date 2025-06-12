package kz.kassen.task_manager.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStatsResponse {
    private Integer done;
    private Integer inProgress;
    private Integer todo;
}
