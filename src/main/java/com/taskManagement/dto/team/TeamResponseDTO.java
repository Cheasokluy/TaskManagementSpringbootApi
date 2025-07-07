package com.taskManagement.dto.team;

import com.taskManagement.dto.team.member.TeamMemberSummaryDTO;
import com.taskManagement.dto.project.ProjectSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String teamCode;
    private String avatarUrl;
    private Integer maxMembers;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Aggregated data
    private Integer currentMemberCount;
    private Integer activeProjectsCount;
    
    // Related entities
    private Set<TeamMemberSummaryDTO> members;
    private Set<ProjectSummaryDTO> projects;
}