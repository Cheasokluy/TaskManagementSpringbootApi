package com.taskManagement.dto.task;

import com.taskManagement.entity.TaskStatus;
import lombok.Data;

@Data
public class TaskStatusUpdateRequest {
    private TaskStatus status;
}
