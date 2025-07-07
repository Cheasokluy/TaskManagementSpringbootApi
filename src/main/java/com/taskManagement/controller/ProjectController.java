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
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO) {
//        log.info("Creating project: {}", projectCreateDTO.getName());
//        try {
//            ProjectResponseDTO createdProject = projectService.createProject(projectCreateDTO);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
//        } catch (IllegalArgumentException e) {
//            log.error("Error creating project: {}", e.getMessage());
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//        } catch (Exception e) {
//            log.error("Unexpected error creating project: ", e);
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Failed to create project: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//        }
        log.info("=== CREATE PROJECT DEBUG ===");
        log.info("Received ProjectCreateDTO: {}", projectCreateDTO);
        log.info("Request name: {}", projectCreateDTO != null ? projectCreateDTO.getName() : "NULL DTO");
        log.info("Request teamId: {}", projectCreateDTO != null ? projectCreateDTO.getTeamId() : "NULL DTO");

        try {
            ProjectResponseDTO createdProject = projectService.createProject(projectCreateDTO);
            log.info("Successfully created project with ID: {}", createdProject.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException creating project: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error creating project: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }
    // Add validation error handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.error("Validation error on field '{}': {}", fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        log.info("Getting project by ID: {}", id);
        try {
            Optional<ProjectResponseDTO> project = projectService.getProjectById(id);
            if (project.isPresent()) {
                return ResponseEntity.ok(project.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Project not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            log.error("Error getting project by ID: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        log.info("Getting all projects");
        List<ProjectResponseDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<ProjectSummaryDTO>> getAllProjectsSummary() {
        log.info("Getting all projects summary");
        List<ProjectSummaryDTO> projects = projectService.getAllProjectsSummary();
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id,
                                           @Valid @RequestBody ProjectUpdateDTO projectUpdateDTO) {
        log.info("Updating project with ID: {}", id);
        try {
            ProjectResponseDTO updatedProject = projectService.updateProject(id, projectUpdateDTO);
            return ResponseEntity.ok(updatedProject);
        } catch (IllegalArgumentException e) {
            log.error("Error updating project: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error updating project: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        log.info("Deleting project with ID: {}", id);
        try {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting project: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error deleting project: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ==================== FILTER & SEARCH OPERATIONS ====================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByStatus(@PathVariable ProjectStatus status) {
        log.info("Getting projects by status: {}", status);
        List<ProjectResponseDTO> projects = projectService.getProjectsByStatus(status);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByPriority(@PathVariable Priority priority) {
        log.info("Getting projects by priority: {}", priority);
        List<ProjectResponseDTO> projects = projectService.getProjectsByPriority(priority);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByTeam(@PathVariable Long teamId) {
        log.info("Getting projects by team ID: {}", teamId);
        List<ProjectResponseDTO> projects = projectService.getProjectsByTeam(teamId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByManager(@PathVariable Long managerId) {
        log.info("Getting projects by manager ID: {}", managerId);
        List<ProjectResponseDTO> projects = projectService.getProjectsByManager(managerId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProjectResponseDTO>> getActiveProjects() {
        log.info("Getting active projects");
        List<ProjectResponseDTO> projects = projectService.getActiveProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<ProjectResponseDTO>> getOverdueProjects() {
        log.info("Getting overdue projects");
        List<ProjectResponseDTO> projects = projectService.getOverdueProjects();
        return ResponseEntity.ok(projects);
    }

    // ==================== STATUS MANAGEMENT ====================

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateProjectStatus(@PathVariable Long id,
                                                 @RequestBody ProjectStatusUpdateRequest request) {
        log.info("Updating project status for ID: {} to {}", id, request.getStatus());
        try {
            ProjectResponseDTO updatedProject = projectService.updateProjectStatus(id, request.getStatus());
            return ResponseEntity.ok(updatedProject);
        } catch (IllegalArgumentException e) {
            log.error("Error updating project status: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error updating project status: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update project status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<?> updateProjectProgress(@PathVariable Long id,
                                                   @RequestBody ProjectProgressUpdateRequest request) {
        log.info("Updating project progress for ID: {} to {}%", id, request.getProgressPercentage());
        try {
            ProjectResponseDTO updatedProject = projectService.updateProjectProgress(id, request.getProgressPercentage());
            return ResponseEntity.ok(updatedProject);
        } catch (IllegalArgumentException e) {
            log.error("Error updating project progress: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error updating project progress: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update project progress: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
