package com.taskManagement.dto.task;

import com.taskManagement.entity.Priority;
import com.taskManagement.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterDTO {
    private Long projectId;
    private Long assigneeId;
    private Long creatorId;
    private TaskStatus status;
    private Priority priority;
    private Boolean isMilestone;
    private Boolean isOverdue;
    private LocalDateTime dueDateFrom;
    private LocalDateTime dueDateTo;
    private LocalDateTime startDateFrom;
    private LocalDateTime startDateTo;

}
