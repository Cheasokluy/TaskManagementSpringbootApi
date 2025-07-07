package com.taskManagement.service;

import com.taskManagement.dto.project.ProjectCreateDTO;
import com.taskManagement.dto.project.ProjectResponseDTO;
import com.taskManagement.dto.project.ProjectSummaryDTO;
import com.taskManagement.dto.project.ProjectUpdateDTO;
import com.taskManagement.entity.Priority;
import com.taskManagement.entity.Project;
import com.taskManagement.entity.ProjectStatus;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    // ==================== BASIC CRUD OPERATIONS (DTO-based) ====================
    ProjectResponseDTO createProject(ProjectCreateDTO projectCreateDTO);
    Optional<ProjectResponseDTO> getProjectById(Long id);
    List<ProjectResponseDTO> getAllProjects();
    List<ProjectSummaryDTO> getAllProjectsSummary();
    ProjectResponseDTO updateProject(Long id, ProjectUpdateDTO projectUpdateDTO);
    void deleteProject(Long id);

    // ==================== FILTER & SEARCH OPERATIONS ====================
    List<ProjectResponseDTO> getProjectsByStatus(ProjectStatus status);
    List<ProjectResponseDTO> getProjectsByPriority(Priority priority);
    List<ProjectResponseDTO> getProjectsByTeam(Long teamId);
    List<ProjectResponseDTO> getProjectsByManager(Long managerId);
    List<ProjectResponseDTO> getActiveProjects();
    List<ProjectResponseDTO> getOverdueProjects();

    // ==================== STATUS MANAGEMENT ====================
    ProjectResponseDTO updateProjectStatus(Long id, ProjectStatus status);
    ProjectResponseDTO updateProjectProgress(Long id, Integer progressPercentage);

    // ==================== VALIDATION & UTILITY ====================
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    // ==================== INTERNAL METHODS (Entity-based for other services) ====================
    Project findProjectEntityById(Long id);
    Project createProjectEntity(Project project);
    List<Project> findAllProjectEntities();

}
