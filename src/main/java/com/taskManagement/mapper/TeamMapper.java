package com.taskManagement.mapper;

import com.taskManagement.dto.project.ProjectSummaryDTO;
import com.taskManagement.dto.team.*;
import com.taskManagement.dto.team.member.TeamMemberSummaryDTO;
import com.taskManagement.dto.user.UserSummaryDTO;
import com.taskManagement.entity.*;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TeamMapper {
    // ==================== CREATE OPERATIONS ====================

    public Team toEntity(TeamCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Team team = new Team();
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());
        team.setTeamCode(dto.getTeamCode());
        team.setAvatarUrl(dto.getAvatarUrl());
        team.setMaxMembers(dto.getMaxMembers());
        team.setIsActive(dto.getIsActive());

        return team;
    }

    // ==================== READ OPERATIONS ====================

    public TeamResponseDTO toResponseDTO(Team team) {
        if (team == null) {
            return null;
        }

        return TeamResponseDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .teamCode(team.getTeamCode())
                .avatarUrl(team.getAvatarUrl())
                .maxMembers(team.getMaxMembers())
                .isActive(team.getIsActive())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .currentMemberCount(calculateCurrentMemberCount(team.getMembers()))
                .activeProjectsCount(calculateActiveProjectsCount(team.getProjects()))
                .members(toTeamMemberSummarySet(team.getMembers()))
                .projects(toProjectSummarySet(team.getProjects()))
                .build();
    }

    public TeamSummaryDTO toSummaryDTO(Team team) {
        if (team == null) {
            return null;
        }

        return TeamSummaryDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .teamCode(team.getTeamCode())
                .avatarUrl(team.getAvatarUrl())
                .maxMembers(team.getMaxMembers())
                .isActive(team.getIsActive())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .currentMemberCount(calculateCurrentMemberCount(team.getMembers()))
                .activeProjectsCount(calculateActiveProjectsCount(team.getProjects()))
                .completedProjectsCount(calculateCompletedProjectsCount(team.getProjects()))
                .build();
    }

    // ==================== UPDATE OPERATIONS ====================

    public void updateEntityFromDTO(Team team, TeamUpdateDTO dto) {
        if (dto == null || team == null) {
            return;
        }

        if (dto.getName() != null) {
            team.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            team.setDescription(dto.getDescription());
        }
        if (dto.getAvatarUrl() != null) {
            team.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getMaxMembers() != null) {
            team.setMaxMembers(dto.getMaxMembers());
        }
        if (dto.getIsActive() != null) {
            team.setIsActive(dto.getIsActive());
        }
    }

    // ==================== LIST OPERATIONS ====================

    public List<TeamResponseDTO> toResponseDTOList(List<Team> teams) {
        if (teams == null) {
            return null;
        }
        return teams.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TeamSummaryDTO> toSummaryDTOList(List<Team> teams) {
        if (teams == null) {
            return null;
        }
        return teams.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    public Set<TeamMemberSummaryDTO> toTeamMemberSummarySet(Set<TeamMember> members) {
        if (members == null) {
            return null;
        }
        return members.stream()
                .filter(member -> member.getIsActive())
                .map(this::toTeamMemberSummaryDTO)
                .collect(Collectors.toSet());
    }

    public Set<ProjectSummaryDTO> toProjectSummarySet(Set<Project> projects) {
        if (projects == null) {
            return null;
        }
        return projects.stream()
                .map(this::toProjectSummaryDTO)
                .collect(Collectors.toSet());
    }

    // ==================== HELPER METHODS ====================

    private Integer calculateCurrentMemberCount(Set<TeamMember> members) {
        if (members == null) {
            return 0;
        }
        return (int) members.stream()
                .filter(member -> member.getIsActive())
                .count();
    }

    private Integer calculateActiveProjectsCount(Set<Project> projects) {
        if (projects == null) {
            return 0;
        }
        return (int) projects.stream()
                .filter(project -> project.getStatus() == ProjectStatus.IN_PROGRESS ||
                        project.getStatus() == ProjectStatus.PLANNING)
                .count();
    }

    private Integer calculateCompletedProjectsCount(Set<Project> projects) {
        if (projects == null) {
            return 0;
        }
        return (int) projects.stream()
                .filter(project -> project.getStatus() == ProjectStatus.COMPLETED)
                .count();
    }

    public TeamMemberSummaryDTO toTeamMemberSummaryDTO(TeamMember member) {
        if (member == null || member.getUser() == null) {
            return null;
        }

        User user = member.getUser();
        return TeamMemberSummaryDTO.builder()
                .id(member.getId())
                .userId(user.getId())
                .userName(user.getUsername())
                .userEmail(user.getEmail())
                .userAvatarUrl(user.getProfilePictureUrl())
                .role(member.getRole())
                .isActive(member.getIsActive())
                .joinedAt(member.getJoinedAt())
                .build();
    }

    private ProjectSummaryDTO toProjectSummaryDTO(Project project) {
        if (project == null) {
            return null;
        }

        return ProjectSummaryDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .priority(project.getPriority())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .progressPercentage(project.getProgressPercentage())
                .colorCode(project.getColorCode())
                .teamName(project.getTeam() != null ? project.getTeam().getName() : null)
                .projectManagerName(project.getProjectManager() != null ?
                        project.getProjectManager().getFullName() : null)
                .taskCount(project.getTasks() != null ? project.getTasks().size() : 0)
                .isOverdue(isProjectOverdue(project))
                .build();
    }

    private Boolean isProjectOverdue(Project project) {
        if (project.getEndDate() == null) {
            return false;
        }
        return java.time.LocalDateTime.now().isAfter(project.getEndDate()) &&
                !project.getStatus().equals(ProjectStatus.COMPLETED);
    }

    // ==================== SPECIALIZED CONVERSIONS ====================

    public TeamSummaryDTO toTeamSummaryForProject(Team team) {
        if (team == null) {
            return null;
        }

        return TeamSummaryDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .teamCode(team.getTeamCode())
                .avatarUrl(team.getAvatarUrl())
                .currentMemberCount(calculateCurrentMemberCount(team.getMembers()))
                .isActive(team.getIsActive())
                .build();
    }

    public TeamSummaryDTO toTeamSummaryForMember(Team team) {
        if (team == null) {
            return null;
        }

        return TeamSummaryDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .teamCode(team.getTeamCode())
                .avatarUrl(team.getAvatarUrl())
                .maxMembers(team.getMaxMembers())
                .currentMemberCount(calculateCurrentMemberCount(team.getMembers()))
                .isActive(team.getIsActive())
                .build();
    }

}
