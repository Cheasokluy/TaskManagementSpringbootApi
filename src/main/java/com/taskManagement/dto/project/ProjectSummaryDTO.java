package com.taskManagement.dto.project;

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

public class ProjectSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private Priority priority;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer progressPercentage;
    private String colorCode;
    private String teamName;
    private String projectManagerName;
    private Integer taskCount;
    private Boolean isOverdue;

}
