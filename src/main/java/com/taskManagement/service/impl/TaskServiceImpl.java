package com.taskManagement.service.impl;

import com.taskManagement.dto.task.*;
import com.taskManagement.entity.Priority;
import com.taskManagement.entity.Task;
import com.taskManagement.entity.TaskStatus;
import com.taskManagement.entity.User;
import com.taskManagement.exception.ResourceNotFoundException;
import com.taskManagement.exception.BadRequestException;
import com.taskManagement.exception.UnauthorizedException;
import com.taskManagement.mapper.task.*;
import com.taskManagement.repository.TaskRepository;
import com.taskManagement.service.TaskService;
import com.taskManagement.service.UserService;
import com.taskManagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final ProjectService projectService;

    // ==================== BASIC CRUD OPERATIONS ====================

    @Override
    public TaskResponseDTO createTask(TaskCreateDTO createDTO) {
        log.info("Creating new task: {}", createDTO.getTitle());
        
        validateTaskCreateDTO(createDTO);
        
        Task task = taskMapper.toEntity(createDTO);
        
        // Load and set full entities
        task.setProject(projectService.findProjectEntityById(createDTO.getProjectId()));
        task.setCreator(userService.findUserEntityById(createDTO.getCreatorId()));
        
        if (createDTO.getAssigneeId() != null) {
            task.setAssignee(userService.findUserEntityById(createDTO.getAssigneeId()));
        }
        
        if (createDTO.getParentTaskId() != null) {
            Task parentTask = findTaskEntityById(createDTO.getParentTaskId());
            validateParentTask(parentTask, task);
            task.setParentTask(parentTask);
        }
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());
        
        return taskMapper.toResponseDTO(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        log.debug("Fetching task with ID: {}", id);
        Task task = findTaskEntityById(id);
        return taskMapper.toResponseDTO(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks() {
        log.debug("Fetching all tasks");
        List<Task> tasks = taskRepository.findAll();
        return taskMapper.toResponseDTOList(tasks);
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO updateDTO) {
        log.info("Updating task with ID: {}", id);
        
        Task existingTask = findTaskEntityById(id);
        validateTaskUpdateDTO(updateDTO, existingTask);
        
        taskMapper.updateEntityFromDTO(existingTask, updateDTO);
        
        // Handle assignee update
        if (updateDTO.getAssigneeId() != null) {
            User newAssignee = userService.findUserEntityById(updateDTO.getAssigneeId());
            existingTask.setAssignee(newAssignee);
        }
        
        // Handle parent task update
        if (updateDTO.getParentTaskId() != null) {
            Task newParentTask = findTaskEntityById(updateDTO.getParentTaskId());
            validateParentTask(newParentTask, existingTask);
            existingTask.setParentTask(newParentTask);
        }
        
        Task updatedTask = taskRepository.save(existingTask);
        log.info("Task updated successfully: {}", updatedTask.getId());
        
        return taskMapper.toResponseDTO(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        log.info("Deleting task with ID: {}", id);
        
        Task task = findTaskEntityById(id);
        
        if (task.getSubtasks() != null && !task.getSubtasks().isEmpty()) {
            throw new BadRequestException("Cannot delete task with existing subtasks");
        }
        
        taskRepository.delete(task);
        log.info("Task deleted successfully: {}", id);
    }

    // ==================== TASKS BY PROJECT ====================

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksByProjectId(Long projectId) {
        log.debug("Fetching tasks for project: {}", projectId);
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksByProjectIdOrderByDate(Long projectId) {
        log.debug("Fetching tasks for project ordered by date: {}", projectId);
        List<Task> tasks = taskRepository.findByProjectIdOrderByDueDateAsc(projectId);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskSummaryDTO> getTasksByProjectIdPaginated(Long projectId, Pageable pageable) {
        log.debug("Fetching paginated tasks for project: {}", projectId);
        Page<Task> taskPage = taskRepository.findByProjectId(projectId, pageable);
        return taskPage.map(taskMapper::toSummaryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksByProjectIdAndStatus(Long projectId, TaskStatus status) {
        log.debug("Fetching tasks for project: {} with status: {}", projectId, status);
        List<Task> tasks = taskRepository.findByProjectIdAndStatus(projectId, status);
        return taskMapper.toSummaryDTOList(tasks);
    }

    // ==================== TASKS BY USER ====================

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksByAssigneeId(Long assigneeId) {
        log.debug("Fetching tasks for assignee: {}", assigneeId);
        List<Task> tasks = taskRepository.findByAssigneeId(assigneeId);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksByAssigneeIdOrderByDueDate(Long assigneeId) {
        log.debug("Fetching tasks for assignee ordered by due date: {}", assigneeId);
        List<Task> tasks = taskRepository.findByAssigneeIdOrderByDueDateAsc(assigneeId);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksByCreatorId(Long creatorId) {
        log.debug("Fetching tasks created by user: {}", creatorId);
        List<Task> tasks = taskRepository.findByCreatorId(creatorId);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksByAssigneeIdAndStatus(Long assigneeId, TaskStatus status) {
        log.debug("Fetching tasks for assignee: {} with status: {}", assigneeId, status);
        List<Task> tasks = taskRepository.findByAssigneeIdAndStatus(assigneeId, status);
        return taskMapper.toSummaryDTOList(tasks);
    }

    // ==================== TASK SCHEDULING AND DEADLINES ====================

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksDueInRange(Long assigneeId, LocalDateTime start, LocalDateTime end) {
        log.debug("Fetching tasks due between {} and {} for assignee: {}", start, end, assigneeId);
        List<Task> tasks = taskRepository.findByAssigneeIdAndDueDateBetween(assigneeId, start, end);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getOverdueTasks(Long assigneeId) {
        log.debug("Fetching overdue tasks for assignee: {}", assigneeId);
        List<Task> tasks = taskRepository.findByAssigneeIdAndDueDateBeforeAndStatusNot(
            assigneeId, LocalDateTime.now(), TaskStatus.COMPLETED);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksDueSoon(Long assigneeId, int daysAhead) {
        log.debug("Fetching tasks due soon for assignee: {}", assigneeId);
        LocalDateTime endDate = LocalDateTime.now().plusDays(daysAhead);
        List<Task> tasks = taskRepository.findByAssigneeIdAndDueDateBetweenAndStatusNot(
            assigneeId, LocalDateTime.now(), endDate, TaskStatus.COMPLETED);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getAllOverdueTasks() {
        log.debug("Fetching all overdue tasks");
        List<Task> tasks = taskRepository.findByDueDateBeforeAndStatusNot(
            LocalDateTime.now(), TaskStatus.COMPLETED);
        return taskMapper.toSummaryDTOList(tasks);
    }


    // ==================== TASK STATUS MANAGEMENT ====================

    @Override
    public TaskResponseDTO updateTaskStatus(Long id, TaskStatus status) {
        log.info("Updating task status: {} to {}", id, status);
        
        Task task = findTaskEntityById(id);
        TaskStatus previousStatus = task.getStatus();
        
        task.setStatus(status);
        
        // Handle status-specific logic
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
            task.setProgressPercentage(100);
        } else if (status == TaskStatus.IN_PROGRESS && previousStatus == TaskStatus.TODO) {
            task.setStartDate(LocalDateTime.now());
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task status updated successfully: {}", updatedTask.getId());
        
        return taskMapper.toResponseDTO(updatedTask);
    }

    @Override
    public TaskResponseDTO updateTaskPriority(Long id, Priority priority) {
        log.info("Updating task priority: {} to {}", id, priority);
        
        Task task = findTaskEntityById(id);
        task.setPriority(priority);
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task priority updated successfully: {}", updatedTask.getId());
        
        return taskMapper.toResponseDTO(updatedTask);
    }

    @Override
    public TaskResponseDTO assignTask(Long taskId, Long assigneeId) {
        log.info("Assigning task: {} to user: {}", taskId, assigneeId);
        
        Task task = findTaskEntityById(taskId);
        User assignee = userService.findUserEntityById(assigneeId);
        
        task.setAssignee(assignee);
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task assigned successfully: {}", updatedTask.getId());
        
        return taskMapper.toResponseDTO(updatedTask);
    }

    @Override
    public TaskResponseDTO unassignTask(Long taskId) {
        log.info("Unassigning task: {}", taskId);
        
        Task task = findTaskEntityById(taskId);
        task.setAssignee(null);
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task unassigned successfully: {}", updatedTask.getId());
        
        return taskMapper.toResponseDTO(updatedTask);
    }

    // ==================== SUBTASKS ====================

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getSubtasks(Long parentTaskId) {
        log.debug("Fetching subtasks for parent task: {}", parentTaskId);
        List<Task> subtasks = taskRepository.findByParentTaskId(parentTaskId);
        return taskMapper.toSummaryDTOList(subtasks);
    }

    @Override
    public TaskResponseDTO createSubtask(Long parentTaskId, TaskCreateDTO subtaskDTO) {
        log.info("Creating subtask for parent task: {}", parentTaskId);
        
        Task parentTask = findTaskEntityById(parentTaskId);
        subtaskDTO.setParentTaskId(parentTaskId);
        subtaskDTO.setProjectId(parentTask.getProject().getId());
        
        return createTask(subtaskDTO);
    }

    // ==================== TASK STATISTICS ====================

    @Override
    @Transactional(readOnly = true)
    public long countTasksByProjectAndStatus(Long projectId, TaskStatus status) {
        log.debug("Counting tasks for project: {} with status: {}", projectId, status);
        return taskRepository.countByProjectIdAndStatus(projectId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTaskCompletionPercentage(Long projectId) {
        log.debug("Calculating completion percentage for project: {}", projectId);
        
        long totalTasks = taskRepository.countByProjectId(projectId);
        if (totalTasks == 0) return 0.0;
        
        long completedTasks = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.COMPLETED);
        return ((double) completedTasks / totalTasks) * 100.0;
    }

    // ==================== TIME TRACKING ====================

    @Override
    public TaskResponseDTO startTimer(Long taskId) {
        log.info("Starting timer for task: {}", taskId);
        
        Task task = findTaskEntityById(taskId);
        
        if (task.getStatus() == TaskStatus.TODO) {
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setStartDate(LocalDateTime.now());
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Timer started for task: {}", updatedTask.getId());
        
        return taskMapper.toResponseDTO(updatedTask);
    }

    @Override
    public TaskResponseDTO stopTimer(Long taskId) {
        log.info("Stopping timer for task: {}", taskId);
        
        Task task = findTaskEntityById(taskId);
        // Timer logic would be implemented here
        // For now, we'll just save the task
        
        Task updatedTask = taskRepository.save(task);
        log.info("Timer stopped for task: {}", updatedTask.getId());
        
        return taskMapper.toResponseDTO(updatedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalTimeSpent(Long taskId) {
        log.debug("Getting total time spent for task: {}", taskId);
        Task task = findTaskEntityById(taskId);
        return task.getActualHours() != null ? task.getActualHours() : 0.0;
    }

    // ==================== TASK VALIDATION ====================

    @Override
    @Transactional(readOnly = true)
    public boolean canUserAccessTask(Long userId, Long taskId) {
        log.debug("Checking access for user: {} to task: {}", userId, taskId);
        Task task = findTaskEntityById(taskId);
        
        return task.getCreator().getId().equals(userId) ||
               (task.getAssignee() != null && task.getAssignee().getId().equals(userId)) ||
               // Add project member check if needed
               true; // For now, allow access
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserEditTask(Long userId, Long taskId) {
        log.debug("Checking edit permission for user: {} to task: {}", userId, taskId);
        Task task = findTaskEntityById(taskId);
        
        return task.getCreator().getId().equals(userId) ||
               // Add project admin check if needed
               true; // For now, allow edit
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTaskOverdue(Long taskId) {
        log.debug("Checking if task is overdue: {}", taskId);
        Task task = findTaskEntityById(taskId);
        
        return task.getDueDate() != null && 
               LocalDateTime.now().isAfter(task.getDueDate()) &&
               task.getStatus() != TaskStatus.COMPLETED;
    }

    // ==================== INTERNAL METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public Task findTaskEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
    }

    // ==================== PRIVATE VALIDATION METHODS ====================

    private void validateTaskCreateDTO(TaskCreateDTO dto) {
        if (dto.getProjectId() == null) {
            throw new BadRequestException("Project ID is required");
        }
        
        if (dto.getCreatorId() == null) {
            throw new BadRequestException("Creator ID is required");
        }
        
        if (dto.getDueDate() != null && dto.getStartDate() != null) {
            if (dto.getDueDate().isBefore(dto.getStartDate())) {
                throw new BadRequestException("Due date cannot be before start date");
            }
        }
    }

    private void validateTaskUpdateDTO(TaskUpdateDTO dto, Task existingTask) {
        if (dto.getDueDate() != null && dto.getStartDate() != null) {
            if (dto.getDueDate().isBefore(dto.getStartDate())) {
                throw new BadRequestException("Due date cannot be before start date");
            }
        }
        
        if (dto.getProgressPercentage() != null && dto.getProgressPercentage() == 100) {
            if (dto.getStatus() != null && dto.getStatus() != TaskStatus.COMPLETED) {
                throw new BadRequestException("Task with 100% progress must be marked as completed");
            }
        }
    }

    private void validateParentTask(Task parentTask, Task childTask) {
        if (parentTask.getId().equals(childTask.getId())) {
            throw new BadRequestException("Task cannot be its own parent");
        }
        
        if (!parentTask.getProject().getId().equals(childTask.getProject().getId())) {
            throw new BadRequestException("Parent task must be in the same project");
        }
    }

    // ==================== NEW METHODS FOR ADDITIONAL FUNCTIONALITY ====================

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getRootTasksByProject(Long projectId) {
        log.debug("Fetching root tasks (no parent) for project: {}", projectId);
        List<Task> tasks = taskRepository.findByProjectIdAndParentTaskIsNull(projectId);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getUnassignedTasksByProject(Long projectId) {
        log.debug("Fetching unassigned tasks for project: {}", projectId);
        List<Task> tasks = taskRepository.findByProjectIdAndAssigneeIsNull(projectId);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getMilestoneTasksByProject(Long projectId) {
        log.debug("Fetching milestone tasks for project: {}", projectId);
        List<Task> tasks = taskRepository.findByProjectIdAndIsMilestoneTrue(projectId);
        return taskMapper.toSummaryDTOList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getTasksRelatedToUser(Long userId) {
        log.debug("Fetching tasks related to user: {}", userId);
    
        List<Task> assignedTasks = taskRepository.findByAssigneeId(userId);
    
        List<Task> createdTasks = taskRepository.findByCreatorId(userId);
    
        // Combine and remove duplicates
        Set<Task> allTasks = new HashSet<>(assignedTasks);
        allTasks.addAll(createdTasks);
    
        // Convert to list and sort by updated date (newest first)
        List<Task> sortedTasks = allTasks.stream()
            .sorted((t1, t2) -> t2.getUpdatedAt().compareTo(t1.getUpdatedAt()))
            .collect(Collectors.toList());
    
        return taskMapper.toSummaryDTOList(sortedTasks);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCompletedTasksByProject(Long projectId) {
        log.debug("Counting completed tasks for project: {}", projectId);
        return taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.COMPLETED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getRecentTasksByProject(Long projectId, int limit) {
        log.debug("Fetching recent {} tasks for project: {}", limit, projectId);
        
        if (limit <= 5) {
            List<Task> tasks = taskRepository.findTop5ByProjectIdOrderByCreatedAtDesc(projectId);
            return taskMapper.toSummaryDTOList(tasks);
        } else {
            // For larger limits, use findAll with custom sorting
            List<Task> allTasks = taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
            List<Task> limitedTasks = allTasks.stream()
                .limit(limit)
                .collect(Collectors.toList());
            return taskMapper.toSummaryDTOList(limitedTasks);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryDTO> getUpcomingTasksForUser(Long assigneeId, int limit) {
        log.debug("Fetching upcoming {} tasks for user: {}", limit, assigneeId);
        
        if (limit <= 10) {
            List<Task> tasks = taskRepository.findTop10ByAssigneeIdOrderByDueDateAsc(assigneeId);
            return taskMapper.toSummaryDTOList(tasks);
        } else {
            List<Task> allTasks = taskRepository.findByAssigneeIdOrderByDueDateAsc(assigneeId);
            List<Task> limitedTasks = allTasks.stream()
                .limit(limit)
                .collect(Collectors.toList());
            return taskMapper.toSummaryDTOList(limitedTasks);
        }
    }
}