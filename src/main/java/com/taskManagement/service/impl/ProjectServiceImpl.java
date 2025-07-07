
package com.taskManagement.service.impl;

import com.taskManagement.dto.project.*;
import com.taskManagement.entity.*;
import com.taskManagement.mapper.ProjectMapper;
import com.taskManagement.repository.ProjectRepository;
import com.taskManagement.repository.TeamRepository;
import com.taskManagement.repository.UserRepository;
import com.taskManagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    // ==================== BASIC CRUD OPERATIONS ====================

    @Override
    public ProjectResponseDTO createProject(ProjectCreateDTO projectCreateDTO) {
        log.info("Creating project: {}", projectCreateDTO.getName());

        // Validate unique name
        if (existsByName(projectCreateDTO.getName())) {
            throw new IllegalArgumentException("Project name already exists: " + projectCreateDTO.getName());
        }

        // Validate team exists
        Team team = teamRepository.findById(projectCreateDTO.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + projectCreateDTO.getTeamId()));

        // Validate project manager exists (if provided)
        User projectManager = null;
        if (projectCreateDTO.getProjectManagerId() != null) {
            projectManager = userRepository.findById(projectCreateDTO.getProjectManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Project manager not found with ID: " + projectCreateDTO.getProjectManagerId()));
        }

        // Convert DTO to entity
        Project project = projectMapper.toEntity(projectCreateDTO);
        project.setTeam(team);
        project.setProjectManager(projectManager);

        // Save and return DTO
        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDTO(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectResponseDTO> getProjectById(Long id) {
        log.info("Getting project by ID: {}", id);
        return projectRepository.findById(id)
                .map(projectMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAllProjects() {
        log.info("Getting all projects");
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toResponseDTOList(projects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectSummaryDTO> getAllProjectsSummary() {
        log.info("Getting all projects summary");
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toSummaryDTOList(projects);
    }

    @Override
    public ProjectResponseDTO updateProject(Long id, ProjectUpdateDTO projectUpdateDTO) {
        log.info("Updating project with ID: {}", id);

        Project existingProject = findProjectEntityById(id);

        // Validate unique name if changed
        if (projectUpdateDTO.getName() != null &&
                !projectUpdateDTO.getName().equals(existingProject.getName()) &&
                existsByName(projectUpdateDTO.getName())) {
            throw new IllegalArgumentException("Project name already exists: " + projectUpdateDTO.getName());
        }

        // Update team if provided
        if (projectUpdateDTO.getTeamId() != null) {
            Team team = teamRepository.findById(projectUpdateDTO.getTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + projectUpdateDTO.getTeamId()));
            existingProject.setTeam(team);
        }

        if (projectUpdateDTO.getProjectManagerId() != null) {
            User projectManager = userRepository.findById(projectUpdateDTO.getProjectManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Project manager not found with ID: " + projectUpdateDTO.getProjectManagerId()));
            existingProject.setProjectManager(projectManager);
        }

        // Update other fields
        projectMapper.updateEntityFromDTO(existingProject, projectUpdateDTO);

        Project updatedProject = projectRepository.save(existingProject);
        return projectMapper.toResponseDTO(updatedProject);
    }

    @Override
    public void deleteProject(Long id) {
        log.info("Deleting project with ID: {}", id);
        Project project = findProjectEntityById(id);
        projectRepository.delete(project);
    }

    // ==================== FILTER & SEARCH OPERATIONS ====================

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByStatus(ProjectStatus status) {
        log.info("Getting projects by status: {}", status);
        List<Project> projects = projectRepository.findByStatus(status);
        return projectMapper.toResponseDTOList(projects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByPriority(Priority priority) {
        log.info("Getting projects by priority: {}", priority);
        List<Project> projects = projectRepository.findByPriority(priority);
        return projectMapper.toResponseDTOList(projects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByTeam(Long teamId) {
        log.info("Getting projects by team ID: {}", teamId);
        List<Project> projects = projectRepository.findByTeamId(teamId);
        return projectMapper.toResponseDTOList(projects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByManager(Long managerId) {
        log.info("Getting projects by manager ID: {}", managerId);
        List<Project> projects = projectRepository.findByProjectManagerId(managerId);
        return projectMapper.toResponseDTOList(projects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getActiveProjects() {
        log.info("Getting active projects");
        List<Project> projects = projectRepository.findByStatusIn(
                List.of(ProjectStatus.PLANNING, ProjectStatus.IN_PROGRESS));
        return projectMapper.toResponseDTOList(projects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getOverdueProjects() {
        log.info("Getting overdue projects");
        List<Project> projects = projectRepository.findOverdueProjects(LocalDateTime.now());
        return projectMapper.toResponseDTOList(projects);
    }

    // ==================== STATUS MANAGEMENT ====================

    @Override
    public ProjectResponseDTO updateProjectStatus(Long id, ProjectStatus status) {
        log.info("Updating project status for ID: {} to {}", id, status);
        Project project = findProjectEntityById(id);
        project.setStatus(status);
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toResponseDTO(updatedProject);
    }

    @Override
    public ProjectResponseDTO updateProjectProgress(Long id, Integer progressPercentage) {
        log.info("Updating project progress for ID: {} to {}%", id, progressPercentage);
        if (progressPercentage < 0 || progressPercentage > 100) {
            throw new IllegalArgumentException("Progress percentage must be between 0 and 100");
        }

        Project project = findProjectEntityById(id);
        project.setProgressPercentage(progressPercentage);

        // Auto-update status based on progress
        if (progressPercentage == 100 && project.getStatus() != ProjectStatus.COMPLETED) {
            project.setStatus(ProjectStatus.COMPLETED);
        } else if (progressPercentage > 0 && project.getStatus() == ProjectStatus.PLANNING) {
            project.setStatus(ProjectStatus.IN_PROGRESS);
        }

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toResponseDTO(updatedProject);
    }

    // ==================== VALIDATION & UTILITY ====================

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return projectRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Long id) {
        return projectRepository.existsByNameAndIdNot(name, id);
    }

    // ==================== INTERNAL METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public Project findProjectEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + id));
    }

    @Override
    public Project createProjectEntity(Project project) {
        return projectRepository.save(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> findAllProjectEntities() {
        return projectRepository.findAll();
    }
}