package com.taskManagement.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamSummaryDTO {

    private Long id;
    private String name;
    private String description;
    private String teamCode;
    private String avatarUrl;
    private Integer maxMembers;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Aggregated summary data
    private Integer currentMemberCount;
    private Integer activeProjectsCount;
    private Integer completedProjectsCount;
}