package com.taskManagement.service;

import com.taskManagement.dto.task.*;
import com.taskManagement.entity.Priority;
import com.taskManagement.entity.Task;
import com.taskManagement.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    
    // ==================== BASIC CRUD OPERATIONS ====================
    TaskResponseDTO createTask(TaskCreateDTO createDTO);
    TaskResponseDTO getTaskById(Long id);
    List<TaskResponseDTO> getAllTasks();
    TaskResponseDTO updateTask(Long id, TaskUpdateDTO updateDTO);
    void deleteTask(Long id);

    // ==================== TASKS BY PROJECT ====================
    List<TaskSummaryDTO> getTasksByProjectId(Long projectId);
    List<TaskSummaryDTO> getTasksByProjectIdOrderByDate(Long projectId);
    Page<TaskSummaryDTO> getTasksByProjectIdPaginated(Long projectId, Pageable pageable);
    List<TaskSummaryDTO> getTasksByProjectIdAndStatus(Long projectId, TaskStatus status);

    // ==================== TASKS BY USER ====================
    List<TaskSummaryDTO> getTasksByAssigneeId(Long assigneeId);
    List<TaskSummaryDTO> getTasksByAssigneeIdOrderByDueDate(Long assigneeId);
    List<TaskSummaryDTO> getTasksByCreatorId(Long creatorId);
    List<TaskSummaryDTO> getTasksByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    // ==================== TASK SCHEDULING AND DEADLINES ====================
    List<TaskSummaryDTO> getTasksDueInRange(Long assigneeId, LocalDateTime start, LocalDateTime end);
    List<TaskSummaryDTO> getOverdueTasks(Long assigneeId);
    List<TaskSummaryDTO> getTasksDueSoon(Long assigneeId, int daysAhead);
    List<TaskSummaryDTO> getAllOverdueTasks();

    // ==================== TASK STATUS MANAGEMENT ====================
    TaskResponseDTO updateTaskStatus(Long id, TaskStatus status);
    TaskResponseDTO updateTaskPriority(Long id, Priority priority);
    TaskResponseDTO assignTask(Long taskId, Long assigneeId);
    TaskResponseDTO unassignTask(Long taskId);

    // ==================== SUBTASKS ====================
    List<TaskSummaryDTO> getSubtasks(Long parentTaskId);
    TaskResponseDTO createSubtask(Long parentTaskId, TaskCreateDTO subtaskDTO);

    // ==================== TASK STATISTICS ====================
    long countTasksByProjectAndStatus(Long projectId, TaskStatus status);
    Double getTaskCompletionPercentage(Long projectId);

    // ==================== TIME TRACKING ====================
    TaskResponseDTO startTimer(Long taskId);
    TaskResponseDTO stopTimer(Long taskId);
    Double getTotalTimeSpent(Long taskId);

    // ==================== TASK VALIDATION ====================
    boolean canUserAccessTask(Long userId, Long taskId);
    boolean canUserEditTask(Long userId, Long taskId);
    boolean isTaskOverdue(Long taskId);

    // ==================== INTERNAL METHODS ====================
    Task findTaskEntityById(Long id);

    // Add these new method signatures to your TaskService interface:

    // ==================== ADDITIONAL USEFUL METHODS ====================
    List<TaskSummaryDTO> getRootTasksByProject(Long projectId);
    List<TaskSummaryDTO> getUnassignedTasksByProject(Long projectId);
    List<TaskSummaryDTO> getMilestoneTasksByProject(Long projectId);
    List<TaskSummaryDTO> getTasksRelatedToUser(Long userId);
    Long countCompletedTasksByProject(Long projectId);
    List<TaskSummaryDTO> getRecentTasksByProject(Long projectId, int limit);
    List<TaskSummaryDTO> getUpcomingTasksForUser(Long assigneeId, int limit);
}