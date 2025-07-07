package com.taskManagement.dto.task;

import com.taskManagement.entity.Priority;
import com.taskManagement.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TaskUpdateDTO {
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private TaskStatus status;
    private Priority priority;
    private LocalDateTime dueDate;
    private LocalDateTime startDate;

    @DecimalMin(value = "0.0", message = "Estimated hours must be positive")
    @DecimalMax(value = "1000.0", message = "Estimated hours cannot exceed 1000")
    private Double estimatedHours;

    @DecimalMin(value = "0.0", message = "Actual hours must be positive")
    @DecimalMax(value = "1000.0", message = "Actual hours cannot exceed 1000")
    private Double actualHours;

    @Min(value = 0, message = "Progress percentage must be between 0 and 100")
    @Max(value = 100, message = "Progress percentage must be between 0 and 100")
    private Integer progressPercentage;

    private Boolean isMilestone;
    private Long assigneeId;
    private Long parentTaskId;

}
