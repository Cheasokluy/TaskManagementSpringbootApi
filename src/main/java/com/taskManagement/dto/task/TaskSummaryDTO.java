package com.taskManagement.dto.task;

import com.taskManagement.entity.Priority;
import com.taskManagement.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDateTime dueDate;
    private Integer progressPercentage;
    private Boolean isMilestone;
    private String projectName;
    private String assigneeName;
    private String creatorName;
    private Boolean isOverdue;
    private Integer subtaskCount;

}
