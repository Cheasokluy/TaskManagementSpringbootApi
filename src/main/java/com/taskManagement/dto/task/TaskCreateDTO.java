package com.taskManagement.dto.task;

import com.taskManagement.entity.Priority;
import com.taskManagement.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDTO {
    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Task status is required")
    private TaskStatus status = TaskStatus.TODO;

    @NotNull(message = "Priority is required")
    private Priority priority = Priority.MEDIUM;

    private LocalDateTime dueDate;
    private LocalDateTime startDate;

    @DecimalMin(value = "0.0", message = "Estimated hours must be positive")
    @DecimalMax(value = "1000.0", message = "Estimated hours cannot exceed 1000")
    private Double estimatedHours;

    @Min(value = 0, message = "Progress percentage must be between 0 and 100")
    @Max(value = 100, message = "Progress percentage must be between 0 and 100")
    private Integer progressPercentage = 0;

    private Boolean isMilestone = false;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "Creator ID is required")
    private Long creatorId;

    private Long assigneeId;
    private Long parentTaskId;

}
