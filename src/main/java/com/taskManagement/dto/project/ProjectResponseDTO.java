package com.taskManagement.dto.project;

import com.taskManagement.dto.team.TeamSummaryDTO;
import com.taskManagement.dto.user.UserSummaryDTO;
import com.taskManagement.entity.Priority;
import com.taskManagement.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProjectResponseDTO {
    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private Priority priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double budget;
    private Integer progressPercentage;
    private String colorCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested DTOs for relationships
    private TeamSummaryDTO team;
    private UserSummaryDTO projectManager;
    private Integer taskCount;
    private Integer completedTaskCount;

    // Computed fields
    private Long daysRemaining;
    private Boolean isOverdue;
    private Double completionPercentage;

}
