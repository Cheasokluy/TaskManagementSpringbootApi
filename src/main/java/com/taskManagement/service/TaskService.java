package com.taskManagement.service;

import com.taskManagement.entity.Task;
import com.taskManagement.entity.TaskStatus;
import com.taskManagement.entity.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    // Basic CRUD operations
    Task createTask(Task task);

    Optional<Task> getTaskById(Long id);

    List<Task> getAllTasks();

    Task updateTask(Long id, Task task);

    void deleteTask(Long id);

    // Tasks by project
    List<Task> getTasksByProjectId(Long projectId);

    List<Task> getTasksByProjectIdOrderByDate(Long projectId);

    Page<Task> getTasksByProjectId(Long projectId, Pageable pageable);

    List<Task> getTasksByProjectIdAndStatus(Long projectId, TaskStatus status);

    // Tasks by user
    List<Task> getTasksByAssigneeId(Long assigneeId);

    List<Task> getTasksByAssigneeIdOrderByDueDate(Long assigneeId);

    List<Task> getTasksByCreatorId(Long creatorId);

    List<Task> getTasksByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    // Task scheduling and deadlines
    List<Task> getTasksDueInRange(Long assigneeId, LocalDateTime start, LocalDateTime end);

    List<Task> getOverdueTasks(Long assigneeId);

    List<Task> getTasksDueSoon(Long assigneeId, int daysAhead);

    List<Task> getAllOverdueTasks();

    // Task status management
    Task updateTaskStatus(Long id, TaskStatus status);

    Task updateTaskPriority(Long id, Priority priority);

    Task assignTask(Long taskId, Long assigneeId);

    Task unassignTask(Long taskId);

    // Subtasks
    List<Task> getSubtasks(Long parentTaskId);

    Task createSubtask(Long parentTaskId, Task subtask);

    // Task statistics
    long countTasksByProjectAndStatus(Long projectId, TaskStatus status);

    Double getTaskCompletionPercentage(Long projectId);

    // Time tracking
    Task startTimer(Long taskId);

    Task stopTimer(Long taskId);

    Long getTotalTimeSpent(Long taskId);

    // Task validation
    boolean canUserAccessTask(Long userId, Long taskId);

    boolean canUserEditTask(Long userId, Long taskId);

    boolean isTaskOverdue(Long taskId);

}
