package com.taskManagement.repository;

import com.taskManagement.entity.Task;
import com.taskManagement.entity.TaskStatus;
import com.taskManagement.entity.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssigneeId(Long assigneeId);

    List<Task> findByCreatorId(Long creatorId);

    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByAssigneeIdAndDueDateBetween(Long assigneeId, LocalDateTime start, LocalDateTime end);

    List<Task> findByAssigneeIdAndDueDateBeforeAndStatusNot(Long assigneeId, LocalDateTime now, TaskStatus status);

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByParentTaskId(Long parentTaskId);

    List<Task> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<Task> findByAssigneeIdOrderByDueDateAsc(Long assigneeId);

    List<Task> findByStatusAndDueDateBefore(TaskStatus status, LocalDateTime date);


}
