package com.taskManagement.dto.task;

import com.taskManagement.dto.project.ProjectSummaryDTO;
import com.taskManagement.dto.user.UserSummaryDTO;
import com.taskManagement.entity.Priority;
import com.taskManagement.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDateTime dueDate;
    private LocalDateTime startDate;
    private LocalDateTime completedAt;
    private Double estimatedHours;
    private Double actualHours;
    private Integer progressPercentage;
    private Boolean isMilestone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested DTOs for relationships
    private ProjectSummaryDTO project;
    private UserSummaryDTO creator;
    private UserSummaryDTO assignee;
    private TaskSummaryDTO parentTask;

    // Counts and computed fields
    private Integer subtaskCount;
    private Integer completedSubtaskCount;
    private Integer commentCount;
    private Integer attachmentCount;
    private Integer dependencyCount;

    // Computed fields
    private Long daysUntilDue;
    private Boolean isOverdue;
    private Double timeSpentPercentage;
    private List<TaskSummaryDTO> subtasks;

}
