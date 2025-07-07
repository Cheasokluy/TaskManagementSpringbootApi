
package com.taskManagement.controller;

import com.taskManagement.dto.common.ApiResponse;
import com.taskManagement.dto.task.*;
import com.taskManagement.entity.Priority;
import com.taskManagement.entity.TaskStatus;
import com.taskManagement.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Validated
public class TaskController {

    private final TaskService taskService;

    // ==================== BASIC CRUD OPERATIONS ====================

    /**
     * Create a new task
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponseDTO>> createTask(@Valid @RequestBody TaskCreateDTO createDTO) {
        log.info("Creating new task: {}", createDTO.getTitle());
        try {
            TaskResponseDTO createdTask = taskService.createTask(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdTask, "Task created successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error creating task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating task: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create task: " + e.getMessage()));
        }
    }

    /**
     * Get task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> getTaskById(@PathVariable Long id) {
        log.debug("Fetching task with ID: {}", id);
        try {
            TaskResponseDTO task = taskService.getTaskById(id);
            return ResponseEntity.ok(ApiResponse.success(task, "Task found"));
        } catch (Exception e) {
            log.error("Error getting task by ID: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Task not found with ID: " + id));
        }
    }

    /**
     * Get all tasks
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getAllTasks() {
        log.debug("Fetching all tasks");
        try {
            List<TaskResponseDTO> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting all tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get tasks: " + e.getMessage()));
        }
    }

    /**
     * Update existing task
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateDTO updateDTO) {
        log.info("Updating task with ID: {}", id);
        try {
            TaskResponseDTO updatedTask = taskService.updateTask(id, updateDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedTask, "Task updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating task: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update task: " + e.getMessage()));
        }
    }

    /**
     * Delete task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTask(@PathVariable Long id) {
        log.info("Deleting task with ID: {}", id);
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok(ApiResponse.success("Task deleted", "Task deleted successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error deleting task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deleting task: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete task: " + e.getMessage()));
        }
    }

    // ==================== TASKS BY PROJECT ====================

    /**
     * Get all tasks for a specific project
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksByProject(@PathVariable Long projectId) {
        log.debug("Fetching tasks for project: {}", projectId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksByProjectId(projectId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting tasks by project: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get tasks for project: " + e.getMessage()));
        }
    }

    /**
     * Get tasks for a project ordered by due date
     */
    @GetMapping("/project/{projectId}/ordered")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksByProjectOrderedByDate(@PathVariable Long projectId) {
        log.debug("Fetching ordered tasks for project: {}", projectId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksByProjectIdOrderByDate(projectId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting ordered tasks by project: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get ordered tasks: " + e.getMessage()));
        }
    }

    /**
     * Get paginated tasks for a project
     */
    @GetMapping("/project/{projectId}/paginated")
    public ResponseEntity<ApiResponse<Page<TaskSummaryDTO>>> getTasksByProjectPaginated(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.debug("Fetching paginated tasks for project: {} (page: {}, size: {})", projectId, page, size);
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : 
                       Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<TaskSummaryDTO> tasks = taskService.getTasksByProjectIdPaginated(projectId, pageable);
            return ResponseEntity.ok(ApiResponse.success(tasks, "Paginated tasks retrieved"));
        } catch (Exception e) {
            log.error("Error getting paginated tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get paginated tasks: " + e.getMessage()));
        }
    }

    /**
     * Get tasks by project and status
     */
    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksByProjectAndStatus(
            @PathVariable Long projectId,
            @PathVariable TaskStatus status) {
        log.debug("Fetching tasks for project: {} with status: {}", projectId, status);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksByProjectIdAndStatus(projectId, status);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting tasks by project and status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get tasks by status: " + e.getMessage()));
        }
    }

    /**
     * Get root tasks (no parent) for a project
     */
    @GetMapping("/project/{projectId}/root")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getRootTasksByProject(@PathVariable Long projectId) {
        log.debug("Fetching root tasks for project: {}", projectId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getRootTasksByProject(projectId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting root tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get root tasks: " + e.getMessage()));
        }
    }

    /**
     * Get unassigned tasks for a project
     */
    @GetMapping("/project/{projectId}/unassigned")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getUnassignedTasksByProject(@PathVariable Long projectId) {
        log.debug("Fetching unassigned tasks for project: {}", projectId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getUnassignedTasksByProject(projectId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting unassigned tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get unassigned tasks: " + e.getMessage()));
        }
    }

    /**
     * Get milestone tasks for a project
     */
    @GetMapping("/project/{projectId}/milestones")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getMilestoneTasksByProject(@PathVariable Long projectId) {
        log.debug("Fetching milestone tasks for project: {}", projectId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getMilestoneTasksByProject(projectId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting milestone tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get milestone tasks: " + e.getMessage()));
        }
    }

    /**
     * Get recent tasks for a project
     */
    @GetMapping("/project/{projectId}/recent")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getRecentTasksByProject(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "5") int limit) {
        log.debug("Fetching recent {} tasks for project: {}", limit, projectId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getRecentTasksByProject(projectId, limit);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting recent tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get recent tasks: " + e.getMessage()));
        }
    }

    // ==================== TASKS BY USER ====================

    /**
     * Get tasks assigned to a user
     */
    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksByAssignee(@PathVariable Long assigneeId) {
        log.debug("Fetching tasks for assignee: {}", assigneeId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksByAssigneeId(assigneeId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting tasks by assignee: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get tasks for assignee: " + e.getMessage()));
        }
    }

    /**
     * Get tasks for assignee ordered by due date
     */
    @GetMapping("/assignee/{assigneeId}/ordered")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksByAssigneeOrderedByDueDate(@PathVariable Long assigneeId) {
        log.debug("Fetching ordered tasks for assignee: {}", assigneeId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksByAssigneeIdOrderByDueDate(assigneeId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting ordered tasks by assignee: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get ordered tasks: " + e.getMessage()));
        }
    }

    /**
     * Get tasks created by a user
     */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksByCreator(@PathVariable Long creatorId) {
        log.debug("Fetching tasks created by user: {}", creatorId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksByCreatorId(creatorId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting tasks by creator: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get tasks by creator: " + e.getMessage()));
        }
    }

    /**
     * Get tasks by assignee and status
     */
    @GetMapping("/assignee/{assigneeId}/status/{status}")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksByAssigneeAndStatus(
            @PathVariable Long assigneeId,
            @PathVariable TaskStatus status) {
        log.debug("Fetching tasks for assignee: {} with status: {}", assigneeId, status);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksByAssigneeIdAndStatus(assigneeId, status);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting tasks by assignee and status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get tasks by assignee and status: " + e.getMessage()));
        }
    }

    /**
     * Get upcoming tasks for a user
     */
    @GetMapping("/assignee/{assigneeId}/upcoming")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getUpcomingTasksForUser(
            @PathVariable Long assigneeId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("Fetching upcoming {} tasks for user: {}", limit, assigneeId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getUpcomingTasksForUser(assigneeId, limit);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting upcoming tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get upcoming tasks: " + e.getMessage()));
        }
    }

    /**
     * Get all tasks related to a user (assigned or created)
     */
    @GetMapping("/user/{userId}/related")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksRelatedToUser(@PathVariable Long userId) {
        log.debug("Fetching tasks related to user: {}", userId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksRelatedToUser(userId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting tasks related to user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get related tasks: " + e.getMessage()));
        }
    }

    // ==================== TASK SCHEDULING AND DEADLINES ====================

    /**
     * Get tasks due in a specific date range for an assignee
     */
    @GetMapping("/assignee/{assigneeId}/due-range")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksDueInRange(
            @PathVariable Long assigneeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.debug("Fetching tasks due between {} and {} for assignee: {}", start, end, assigneeId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksDueInRange(assigneeId, start, end);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting tasks due in range: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get tasks in date range: " + e.getMessage()));
        }
    }

    /**
     * Get overdue tasks for a specific assignee
     */
    @GetMapping("/assignee/{assigneeId}/overdue")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getOverdueTasksForAssignee(@PathVariable Long assigneeId) {
        log.debug("Fetching overdue tasks for assignee: {}", assigneeId);
        try {
            List<TaskSummaryDTO> tasks = taskService.getOverdueTasks(assigneeId);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting overdue tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get overdue tasks: " + e.getMessage()));
        }
    }

    /**
     * Get tasks due soon for a specific assignee
     */
    @GetMapping("/assignee/{assigneeId}/due-soon")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getTasksDueSoon(
            @PathVariable Long assigneeId,
            @RequestParam(defaultValue = "7") int daysAhead) {
        log.debug("Fetching tasks due soon for assignee: {} (within {} days)", assigneeId, daysAhead);
        try {
            List<TaskSummaryDTO> tasks = taskService.getTasksDueSoon(assigneeId, daysAhead);
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting tasks due soon: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get tasks due soon: " + e.getMessage()));
        }
    }

    /**
     * Get all overdue tasks across the system
     */
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getAllOverdueTasks() {
        log.debug("Fetching all overdue tasks");
        try {
            List<TaskSummaryDTO> tasks = taskService.getAllOverdueTasks();
            return ResponseEntity.ok(ApiResponse.success(tasks, tasks.size()));
        } catch (Exception e) {
            log.error("Error getting all overdue tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get all overdue tasks: " + e.getMessage()));
        }
    }

    // ==================== TASK STATUS MANAGEMENT ====================

    /**
     * Update task status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {
        log.info("Updating task status: {} to {}", id, status);
        try {
            TaskResponseDTO updatedTask = taskService.updateTaskStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(updatedTask, "Task status updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating task status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating task status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update task status: " + e.getMessage()));
        }
    }

    /**
     * Update task priority
     */
    @PatchMapping("/{id}/priority")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTaskPriority(
            @PathVariable Long id,
            @RequestParam Priority priority) {
        log.info("Updating task priority: {} to {}", id, priority);
        try {
            TaskResponseDTO updatedTask = taskService.updateTaskPriority(id, priority);
            return ResponseEntity.ok(ApiResponse.success(updatedTask, "Task priority updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating task priority: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating task priority: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update task priority: " + e.getMessage()));
        }
    }

    /**
     * Assign task to a user
     */
    @PatchMapping("/{taskId}/assign/{assigneeId}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long assigneeId) {
        log.info("Assigning task: {} to user: {}", taskId, assigneeId);
        try {
            TaskResponseDTO updatedTask = taskService.assignTask(taskId, assigneeId);
            return ResponseEntity.ok(ApiResponse.success(updatedTask, "Task assigned successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error assigning task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error assigning task: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to assign task: " + e.getMessage()));
        }
    }

    /**
     * Unassign task
     */
    @PatchMapping("/{taskId}/unassign")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> unassignTask(@PathVariable Long taskId) {
        log.info("Unassigning task: {}", taskId);
        try {
            TaskResponseDTO updatedTask = taskService.unassignTask(taskId);
            return ResponseEntity.ok(ApiResponse.success(updatedTask, "Task unassigned successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error unassigning task: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error unassigning task: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to unassign task: " + e.getMessage()));
        }
    }

    // ==================== SUBTASKS ====================

    /**
     * Get subtasks of a parent task
     */
    @GetMapping("/{parentTaskId}/subtasks")
    public ResponseEntity<ApiResponse<List<TaskSummaryDTO>>> getSubtasks(@PathVariable Long parentTaskId) {
        log.debug("Fetching subtasks for parent task: {}", parentTaskId);
        try {
            List<TaskSummaryDTO> subtasks = taskService.getSubtasks(parentTaskId);
            return ResponseEntity.ok(ApiResponse.success(subtasks, subtasks.size()));
        } catch (Exception e) {
            log.error("Error getting subtasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get subtasks: " + e.getMessage()));
        }
    }

    /**
     * Create a subtask
     */
    @PostMapping("/{parentTaskId}/subtasks")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> createSubtask(
            @PathVariable Long parentTaskId,
            @Valid @RequestBody TaskCreateDTO subtaskDTO) {
        log.info("Creating subtask for parent task: {}", parentTaskId);
        try {
            TaskResponseDTO createdSubtask = taskService.createSubtask(parentTaskId, subtaskDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdSubtask, "Subtask created successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error creating subtask: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating subtask: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create subtask: " + e.getMessage()));
        }
    }

    // ==================== TASK STATISTICS ====================

    /**
     * Count tasks by project and status
     */
    @GetMapping("/project/{projectId}/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countTasksByProjectAndStatus(
            @PathVariable Long projectId,
            @PathVariable TaskStatus status) {
        log.debug("Counting tasks for project: {} with status: {}", projectId, status);
        try {
            long count = taskService.countTasksByProjectAndStatus(projectId, status);
            return ResponseEntity.ok(ApiResponse.success(count, "Task count retrieved"));
        } catch (Exception e) {
            log.error("Error counting tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to count tasks: " + e.getMessage()));
        }
    }

    /**
     * Get task completion percentage for a project
     */
    @GetMapping("/project/{projectId}/completion-percentage")
    public ResponseEntity<ApiResponse<Double>> getTaskCompletionPercentage(@PathVariable Long projectId) {
        log.debug("Calculating completion percentage for project: {}", projectId);
        try {
            Double percentage = taskService.getTaskCompletionPercentage(projectId);
            return ResponseEntity.ok(ApiResponse.success(percentage, "Completion percentage calculated"));
        } catch (Exception e) {
            log.error("Error calculating completion percentage: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to calculate completion percentage: " + e.getMessage()));
        }
    }

    /**
     * Count completed tasks by project
     */
    @GetMapping("/project/{projectId}/count/completed")
    public ResponseEntity<ApiResponse<Long>> countCompletedTasksByProject(@PathVariable Long projectId) {
        log.debug("Counting completed tasks for project: {}", projectId);
        try {
            Long count = taskService.countCompletedTasksByProject(projectId);
            return ResponseEntity.ok(ApiResponse.success(count, "Completed tasks counted"));
        } catch (Exception e) {
            log.error("Error counting completed tasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to count completed tasks: " + e.getMessage()));
        }
    }

    // ==================== TIME TRACKING ====================

    /**
     * Start timer for a task
     */
    @PostMapping("/{taskId}/timer/start")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> startTimer(@PathVariable Long taskId) {
        log.info("Starting timer for task: {}", taskId);
        try {
            TaskResponseDTO updatedTask = taskService.startTimer(taskId);
            return ResponseEntity.ok(ApiResponse.success(updatedTask, "Timer started successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error starting timer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error starting timer: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to start timer: " + e.getMessage()));
        }
    }

    /**
     * Stop timer for a task
     */
    @PostMapping("/{taskId}/timer/stop")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> stopTimer(@PathVariable Long taskId) {
        log.info("Stopping timer for task: {}", taskId);
        try {
            TaskResponseDTO updatedTask = taskService.stopTimer(taskId);
            return ResponseEntity.ok(ApiResponse.success(updatedTask, "Timer stopped successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error stopping timer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error stopping timer: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to stop timer: " + e.getMessage()));
        }
    }

    /**
     * Get total time spent on a task
     */
    @GetMapping("/{taskId}/time-spent")
    public ResponseEntity<ApiResponse<Double>> getTotalTimeSpent(@PathVariable Long taskId) {
        log.debug("Getting total time spent for task: {}", taskId);
        try {
            Double timeSpent = taskService.getTotalTimeSpent(taskId);
            return ResponseEntity.ok(ApiResponse.success(timeSpent, "Time spent retrieved"));
        } catch (Exception e) {
            log.error("Error getting time spent: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get time spent: " + e.getMessage()));
        }
    }

    // ==================== TASK VALIDATION ====================

    /**
     * Check if user can access a task
     */
    @GetMapping("/{taskId}/access/user/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> canUserAccessTask(
            @PathVariable Long userId,
            @PathVariable Long taskId) {
        log.debug("Checking access for user: {} to task: {}", userId, taskId);
        try {
            boolean canAccess = taskService.canUserAccessTask(userId, taskId);
            return ResponseEntity.ok(ApiResponse.success(canAccess, "Access check completed"));
        } catch (Exception e) {
            log.error("Error checking user access: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check access: " + e.getMessage()));
        }
    }

    /**
     * Check if user can edit a task
     */
    @GetMapping("/{taskId}/edit/user/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> canUserEditTask(
            @PathVariable Long userId,
            @PathVariable Long taskId) {
        log.debug("Checking edit permission for user: {} to task: {}", userId, taskId);
        try {
            boolean canEdit = taskService.canUserEditTask(userId, taskId);
            return ResponseEntity.ok(ApiResponse.success(canEdit, "Edit permission check completed"));
        } catch (Exception e) {
            log.error("Error checking edit permission: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check edit permission: " + e.getMessage()));
        }
    }

    /**
     * Check if a task is overdue
     */
    @GetMapping("/{taskId}/overdue")
    public ResponseEntity<ApiResponse<Boolean>> isTaskOverdue(@PathVariable Long taskId) {
        log.debug("Checking if task is overdue: {}", taskId);
        try {
            boolean isOverdue = taskService.isTaskOverdue(taskId);
            return ResponseEntity.ok(ApiResponse.success(isOverdue, "Overdue check completed"));
        } catch (Exception e) {
            log.error("Error checking if task is overdue: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check overdue status: " + e.getMessage()));
        }
    }

    // ==================== BULK OPERATIONS ====================

    /**
     * Bulk update task status
     */
    @PatchMapping("/bulk/status")
    public ResponseEntity<ApiResponse<String>> bulkUpdateTaskStatus(
            @RequestParam List<Long> taskIds,
            @RequestParam TaskStatus status) {
        log.info("Bulk updating {} tasks to status: {}", taskIds.size(), status);
        try {
            taskIds.forEach(taskId -> taskService.updateTaskStatus(taskId, status));
            String message = "Successfully updated " + taskIds.size() + " tasks";
            return ResponseEntity.ok(ApiResponse.success(message, "Bulk status update completed"));
        } catch (Exception e) {
            log.error("Error in bulk status update: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update task statuses: " + e.getMessage()));
        }
    }

    /**
     * Bulk assign tasks
     */
    @PatchMapping("/bulk/assign/{assigneeId}")
    public ResponseEntity<ApiResponse<String>> bulkAssignTasks(
            @RequestParam List<Long> taskIds,
            @PathVariable Long assigneeId) {
        log.info("Bulk assigning {} tasks to user: {}", taskIds.size(), assigneeId);
        try {
            taskIds.forEach(taskId -> taskService.assignTask(taskId, assigneeId));
            String message = "Successfully assigned " + taskIds.size() + " tasks";
            return ResponseEntity.ok(ApiResponse.success(message, "Bulk assignment completed"));
        } catch (Exception e) {
            log.error("Error in bulk assignment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to assign tasks: " + e.getMessage()));
        }
    }
}
