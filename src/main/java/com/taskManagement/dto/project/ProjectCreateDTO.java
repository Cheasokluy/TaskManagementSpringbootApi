package com.taskManagement.dto.project;

import com.taskManagement.entity.Priority;
import com.taskManagement.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProjectCreateDTO {
    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private ProjectStatus status = ProjectStatus.PLANNING;

    private Priority priority = Priority.MEDIUM;

    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0")
    private Double budget;

    @Min(value = 0, message = "Progress percentage must be between 0 and 100")
    @Max(value = 100, message = "Progress percentage must be between 0 and 100")
    private Integer progressPercentage = 0;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color code must be a valid hex color (e.g., #FF0000)")
    private String colorCode;

    @NotNull(message = "Team ID is required")
    private Long teamId;

    private Long projectManagerId;

}
