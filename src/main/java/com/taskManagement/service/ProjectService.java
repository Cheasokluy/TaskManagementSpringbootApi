package com.taskManagement.service;

import com.taskManagement.entity.Project;
import com.taskManagement.entity.ProjectStatus;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    // Basic CRUD operations
    Project createProject(Project project);

    Optional<Project> getProjectById(Long id);

    List<Project> getAllProjects();

    Project updateProject(Long id, Project project);

    void deleteProject(Long id);

    // Project by team
    List<Project> getProjectsByTeamId(Long teamId);

    List<Project> getProjectsByTeamIdOrderByDate(Long teamId);

    List<Project> getProjectsByTeamIdAndStatus(Long teamId, ProjectStatus status);

    List<Project> getProjectsByTeamIdAndStatusOrderByDate(Long teamId, ProjectStatus status);

    // Project by manager
    List<Project> getProjectsByManagerId(Long managerId);

    // Project status management
    Project updateProjectStatus(Long id, ProjectStatus status);

    Project updateProjectProgress(Long id, Integer progressPercentage);

    // Project statistics
    long countProjectsByTeamAndStatus(Long teamId, ProjectStatus status);

    Double getTotalBudgetByTeam(Long teamId);

    List<Project> getProjectsDueSoon(Long teamId, int daysAhead);

    // Project validation
    boolean isUserProjectManager(Long userId, Long projectId);

    boolean canUserAccessProject(Long userId, Long projectId);

    // Project assignment
    Project assignProjectManager(Long projectId, Long managerId);

    Project changeTeam(Long projectId, Long newTeamId);

}
