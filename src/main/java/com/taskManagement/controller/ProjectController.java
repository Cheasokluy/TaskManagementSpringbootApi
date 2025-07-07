package com.taskManagement.controller;

import com.taskManagement.dto.project.*;
import com.taskManagement.entity.Priority;
import com.taskManagement.entity.ProjectStatus;
import com.taskManagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Validated


public class ProjectController {

    private final ProjectService projectService;

    // ==================== BASIC CRUD OPERATIONS ====================

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> createProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO) {
        log.info("Creating project: {}", projectCreateDTO.getName());
        try {
            ProjectResponseDTO createdProject = projectService.createProject(projectCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdProject, "Project created successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error creating project: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating project: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create project: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectById(@PathVariable Long id) {
        log.info("Getting project by ID: {}", id);
        try {
            Optional<ProjectResponseDTO> project = projectService.getProjectById(id);
            if (project.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(project.get(), "Project found"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Project not found with ID: " + id));
            }
        } catch (Exception e) {
            log.error("Error getting project by ID: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get project: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getAllProjects() {
        log.info("Getting all projects");
        try {
            List<ProjectResponseDTO> projects = projectService.getAllProjects();
            return ResponseEntity.ok(ApiResponse.success(projects, projects.size()));
        } catch (Exception e) {
            log.error("Error getting all projects: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get projects: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<ProjectSummaryDTO>>> getAllProjectsSummary() {
        log.info("Getting all projects summary");
        try {
            List<ProjectSummaryDTO> projects = projectService.getAllProjectsSummary();
            return ResponseEntity.ok(ApiResponse.success(projects, projects.size()));
        } catch (Exception e) {
            log.error("Error getting projects summary: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get projects summary: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(@PathVariable Long id,
                                                                         @Valid @RequestBody ProjectUpdateDTO projectUpdateDTO) {
        log.info("Updating project with ID: {}", id);
        try {
            ProjectResponseDTO updatedProject = projectService.updateProject(id, projectUpdateDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedProject, "Project updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating project: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating project: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update project: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Long id) {
        log.info("Deleting project with ID: {}", id);
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok(ApiResponse.success("Project deleted", "Project deleted successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error deleting project: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deleting project: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete project: " + e.getMessage()));
        }
    }

    // ==================== FILTER & SEARCH OPERATIONS ====================

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getProjectsByStatus(@PathVariable ProjectStatus status) {
        log.info("Getting projects by status: {}", status);
        try {
            List<ProjectResponseDTO> projects = projectService.getProjectsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(projects, projects.size()));
        } catch (Exception e) {
            log.error("Error getting projects by status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get projects by status: " + e.getMessage()));
        }
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getProjectsByPriority(@PathVariable Priority priority) {
        log.info("Getting projects by priority: {}", priority);
        try {
            List<ProjectResponseDTO> projects = projectService.getProjectsByPriority(priority);
            return ResponseEntity.ok(ApiResponse.success(projects, projects.size()));
        } catch (Exception e) {
            log.error("Error getting projects by priority: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get projects by priority: " + e.getMessage()));
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getProjectsByTeam(@PathVariable Long teamId) {
        log.info("Getting projects by team ID: {}", teamId);
        try {
            List<ProjectResponseDTO> projects = projectService.getProjectsByTeam(teamId);
            return ResponseEntity.ok(ApiResponse.success(projects, projects.size()));
        } catch (Exception e) {
            log.error("Error getting projects by team: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get projects by team: " + e.getMessage()));
        }
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getProjectsByManager(@PathVariable Long managerId) {
        log.info("Getting projects by manager ID: {}", managerId);
        try {
            List<ProjectResponseDTO> projects = projectService.getProjectsByManager(managerId);
            return ResponseEntity.ok(ApiResponse.success(projects, projects.size()));
        } catch (Exception e) {
            log.error("Error getting projects by manager: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get projects by manager: " + e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getActiveProjects() {
        log.info("Getting active projects");
        try {
            List<ProjectResponseDTO> projects = projectService.getActiveProjects();
            return ResponseEntity.ok(ApiResponse.success(projects, projects.size()));
        } catch (Exception e) {
            log.error("Error getting active projects: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get active projects: " + e.getMessage()));
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getOverdueProjects() {
        log.info("Getting overdue projects");
        try {
            List<ProjectResponseDTO> projects = projectService.getOverdueProjects();
            return ResponseEntity.ok(ApiResponse.success(projects, projects.size()));
        } catch (Exception e) {
            log.error("Error getting overdue projects: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get overdue projects: " + e.getMessage()));
        }
    }

    // ==================== STATUS MANAGEMENT ====================

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProjectStatus(@PathVariable Long id,
                                                                               @RequestBody ProjectStatusUpdateRequest request) {
        log.info("Updating project status for ID: {} to {}", id, request.getStatus());
        try {
            ProjectResponseDTO updatedProject = projectService.updateProjectStatus(id, request.getStatus());
            return ResponseEntity.ok(ApiResponse.success(updatedProject, "Project status updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating project status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating project status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update project status: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProjectProgress(@PathVariable Long id,
                                                                                 @RequestBody ProjectProgressUpdateRequest request) {
        log.info("Updating project progress for ID: {} to {}%", id, request.getProgressPercentage());
        try {
            ProjectResponseDTO updatedProject = projectService.updateProjectProgress(id, request.getProgressPercentage());
            return ResponseEntity.ok(ApiResponse.success(updatedProject, "Project progress updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating project progress: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating project progress: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update project progress: " + e.getMessage()));
        }
    }

    // ==================== REQUEST CLASSES ====================

    public static class ProjectStatusUpdateRequest {
        private ProjectStatus status;
        public ProjectStatus getStatus() { return status; }
        public void setStatus(ProjectStatus status) { this.status = status; }
    }

    public static class ProjectProgressUpdateRequest {
        private Integer progressPercentage;
        public Integer getProgressPercentage() { return progressPercentage; }
        public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    }

}
