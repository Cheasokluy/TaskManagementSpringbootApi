package com.taskManagement.mapper;

import com.taskManagement.dto.project.*;
import com.taskManagement.dto.team.TeamSummaryDTO;
import com.taskManagement.dto.user.UserSummaryDTO;
import com.taskManagement.entity.*;
import com.taskManagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {
    public Project toEntity(ProjectCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setPriority(dto.getPriority());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setBudget(dto.getBudget());
        project.setProgressPercentage(dto.getProgressPercentage());
        project.setColorCode(dto.getColorCode());

        return project;
    }

    public ProjectResponseDTO toResponseDTO(Project project) {
        if (project == null) {
            return null;
        }

        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .priority(project.getPriority())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .budget(project.getBudget())
                .progressPercentage(project.getProgressPercentage())
                .colorCode(project.getColorCode())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .team(toTeamSummaryDTO(project.getTeam()))
                .projectManager(toUserSummaryDTO(project.getProjectManager()))
                .taskCount(project.getTasks() != null ? project.getTasks().size() : 0)
                .completedTaskCount(calculateCompletedTasks(project))
                .daysRemaining(calculateDaysRemaining(project.getEndDate()))
                .isOverdue(isProjectOverdue(project))
                .completionPercentage(calculateCompletionPercentage(project))
                .build();
    }

    public ProjectSummaryDTO toSummaryDTO(Project project) {
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

    public void updateEntityFromDTO(Project project, ProjectUpdateDTO dto) {
        if (dto == null || project == null) {
            return;
        }

        if (dto.getName() != null) {
            project.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            project.setStatus(dto.getStatus());
        }
        if (dto.getPriority() != null) {
            project.setPriority(dto.getPriority());
        }
        if (dto.getStartDate() != null) {
            project.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            project.setEndDate(dto.getEndDate());
        }
        if (dto.getBudget() != null) {
            project.setBudget(dto.getBudget());
        }
        if (dto.getProgressPercentage() != null) {
            project.setProgressPercentage(dto.getProgressPercentage());
        }
        if (dto.getColorCode() != null) {
            project.setColorCode(dto.getColorCode());
        }
    }

    public List<ProjectResponseDTO> toResponseDTOList(List<Project> projects) {
        return projects.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectSummaryDTO> toSummaryDTOList(List<Project> projects) {
        return projects.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // Helper methods
    private TeamSummaryDTO toTeamSummaryDTO(Team team) {
        if (team == null) {
            return null;
        }

        return TeamSummaryDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .currentMemberCount(team.getMembers() != null ? team.getMembers().size() : 0)
                .build();
    }

    private UserSummaryDTO toUserSummaryDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserSummaryDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }

    private Integer calculateCompletedTasks(Project project) {
        if (project.getTasks() == null) {
            return 0;
        }
        return (int) project.getTasks().stream()
                .filter(task -> "COMPLETED".equals(task.getStatus().toString()))
                .count();
    }

    private Long calculateDaysRemaining(LocalDateTime endDate) {
        if (endDate == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
    }

    private Boolean isProjectOverdue(Project project) {
        if (project.getEndDate() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(project.getEndDate()) &&
                !project.getStatus().equals(ProjectStatus.COMPLETED);
    }

    private Double calculateCompletionPercentage(Project project) {
        if (project.getTasks() == null || project.getTasks().isEmpty()) {
            return 0.0;
        }

        int totalTasks = project.getTasks().size();
        int completedTasks = calculateCompletedTasks(project);

        return (double) completedTasks / totalTasks * 100;
    }

}
