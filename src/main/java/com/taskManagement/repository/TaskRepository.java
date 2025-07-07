package com.taskManagement.repository;

import com.taskManagement.entity.Task;
import com.taskManagement.entity.TaskStatus;
import com.taskManagement.entity.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // ==================== BASIC FINDER METHODS ====================
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByCreatorId(Long creatorId);
    List<Task> findByParentTaskId(Long parentTaskId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByPriority(Priority priority);
    
    // ==================== COMPOUND QUERIES ====================
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
    List<Task> findByProjectIdAndAssigneeId(Long projectId, Long assigneeId);
    
    // ==================== DATE RANGE QUERIES ====================
    List<Task> findByAssigneeIdAndDueDateBetween(Long assigneeId, LocalDateTime start, LocalDateTime end);
    List<Task> findByAssigneeIdAndDueDateBeforeAndStatusNot(Long assigneeId, LocalDateTime now, TaskStatus status);
    List<Task> findByAssigneeIdAndDueDateBetweenAndStatusNot(Long assigneeId, LocalDateTime start, LocalDateTime end, TaskStatus status);
    List<Task> findByDueDateBeforeAndStatusNot(LocalDateTime now, TaskStatus status);
    List<Task> findByStatusAndDueDateBefore(TaskStatus status, LocalDateTime date);
    
    // ==================== PAGINATED QUERIES ====================
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    
    // ==================== ORDERED QUERIES ====================
    List<Task> findByProjectIdOrderByCreatedAtDesc(Long projectId);
    List<Task> findByProjectIdOrderByDueDateAsc(Long projectId);
    List<Task> findByAssigneeIdOrderByDueDateAsc(Long assigneeId);
    List<Task> findByProjectIdOrderByPriorityDesc(Long projectId);
    List<Task> findByAssigneeIdOrderByPriorityDesc(Long assigneeId);
    
    // ==================== NULL/NOT NULL QUERIES ====================
    List<Task> findByProjectIdAndParentTaskIsNull(Long projectId); // Root tasks
    List<Task> findByAssigneeIsNull(); // Unassigned tasks
    List<Task> findByProjectIdAndAssigneeIsNull(Long projectId); // Unassigned tasks in project
    List<Task> findByDueDateIsNotNullAndStatusNot(TaskStatus status); // Tasks with due dates that are not completed
    
    // ==================== COUNTING METHODS ====================
    long countByProjectId(Long projectId);
    long countByProjectIdAndStatus(Long projectId, TaskStatus status);
    long countByAssigneeId(Long assigneeId);
    long countByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);
    long countByStatus(TaskStatus status);
    long countByProjectIdAndParentTaskIsNull(Long projectId); // Count root tasks
    
    // ==================== EXISTENCE CHECKS ====================
    boolean existsByProjectIdAndTitle(Long projectId, String title);
    boolean existsByParentTaskId(Long parentTaskId);
    boolean existsByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);
    
    // ==================== ADDITIONAL USEFUL METHODS ====================
    List<Task> findTop10ByAssigneeIdOrderByDueDateAsc(Long assigneeId); // Next 10 tasks by due date
    List<Task> findTop5ByProjectIdOrderByCreatedAtDesc(Long projectId); // Recent 5 tasks in project
    List<Task> findByProjectIdAndStatusInOrderByPriorityDesc(Long projectId, List<TaskStatus> statuses); // Multiple statuses
    List<Task> findByAssigneeIdAndStatusInOrderByDueDateAsc(Long assigneeId, List<TaskStatus> statuses);
    
    // ==================== MILESTONE TASKS ====================
    List<Task> findByProjectIdAndIsMilestoneTrue(Long projectId);
    List<Task> findByAssigneeIdAndIsMilestoneTrue(Long assigneeId);
    long countByProjectIdAndIsMilestoneTrue(Long projectId);
    
    // ==================== PROGRESS TRACKING ====================
    List<Task> findByProgressPercentageGreaterThan(Integer percentage);
    List<Task> findByProgressPercentageBetween(Integer minPercentage, Integer maxPercentage);
    List<Task> findByProjectIdAndProgressPercentageLessThan(Long projectId, Integer percentage);
}