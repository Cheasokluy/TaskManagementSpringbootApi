package com.taskManagement.mapper.task;

import com.taskManagement.dto.task.*;
import com.taskManagement.entity.Task;
import com.taskManagement.entity.User;
import com.taskManagement.entity.Project;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public  class TaskMapper {
    // ==================== CREATE OPERATIONS ====================

    public Task toEntity(TaskCreateDTO dto) {
        if (dto == null) return null;

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setStartDate(dto.getStartDate());
        task.setEstimatedHours(dto.getEstimatedHours());
        task.setProgressPercentage(dto.getProgressPercentage());
        task.setIsMilestone(dto.getIsMilestone());
        task.setActualHours(0.0);

        // Set relationships (IDs only for now)
        if (dto.getProjectId() != null) {
            Project project = new Project();
            project.setId(dto.getProjectId());
            task.setProject(project);
        }

        if (dto.getCreatorId() != null) {
            User creator = new User();
            creator.setId(dto.getCreatorId());
            task.setCreator(creator);
        }

        if (dto.getAssigneeId() != null) {
            User assignee = new User();
            assignee.setId(dto.getAssigneeId());
            task.setAssignee(assignee);
        }

        if (dto.getParentTaskId() != null) {
            Task parentTask = new Task();
            parentTask.setId(dto.getParentTaskId());
            task.setParentTask(parentTask);
        }

        return task;
    }

    // ==================== READ OPERATIONS ====================

    public TaskResponseDTO toResponseDTO(Task task) {
        if (task == null) return null;

        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .startDate(task.getStartDate())
                .completedAt(task.getCompletedAt())
                .estimatedHours(task.getEstimatedHours())
                .actualHours(task.getActualHours())
                .progressPercentage(task.getProgressPercentage())
                .isMilestone(task.getIsMilestone())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .project(task.getProject() != null ? toProjectSummary(task.getProject()) : null)
                .creator(task.getCreator() != null ? toUserSummary(task.getCreator()) : null)
                .assignee(task.getAssignee() != null ? toUserSummary(task.getAssignee()) : null)
                .parentTask(task.getParentTask() != null ? toSummaryDTO(task.getParentTask()) : null)
                .subtaskCount(calculateSubtaskCount(task.getSubtasks()))
                .completedSubtaskCount(calculateCompletedSubtaskCount(task.getSubtasks()))
                .commentCount(calculateCommentCount(task.getComments()))
                .attachmentCount(calculateAttachmentCount(task.getAttachments()))
                .dependencyCount(calculateDependencyCount(task.getDependencies()))
                .daysUntilDue(calculateDaysUntilDue(task.getDueDate()))
                .isOverdue(calculateIsOverdue(task.getDueDate()))
                .timeSpentPercentage(calculateTimeSpentPercentage(task))
                .subtasks(subtasksToSummaryList(task.getSubtasks()))
                .build();
    }

    public TaskSummaryDTO toSummaryDTO(Task task) {
        if (task == null) return null;

        return TaskSummaryDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .progressPercentage(task.getProgressPercentage())
                .isMilestone(task.getIsMilestone())
                .projectName(task.getProject() != null ? task.getProject().getName() : null)
                .assigneeName(task.getAssignee() != null ? task.getAssignee().getFullName() : null)
                .creatorName(task.getCreator() != null ? task.getCreator().getFullName() : null)
                .isOverdue(calculateIsOverdue(task.getDueDate()))
                .subtaskCount(calculateSubtaskCount(task.getSubtasks()))
                .build();
    }

    // ==================== UPDATE OPERATIONS ====================

    public void updateEntityFromDTO(Task task, TaskUpdateDTO dto) {
        if (dto == null || task == null) return;

        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
        if (dto.getStartDate() != null) task.setStartDate(dto.getStartDate());
        if (dto.getEstimatedHours() != null) task.setEstimatedHours(dto.getEstimatedHours());
        if (dto.getActualHours() != null) task.setActualHours(dto.getActualHours());
        if (dto.getProgressPercentage() != null) task.setProgressPercentage(dto.getProgressPercentage());
        if (dto.getIsMilestone() != null) task.setIsMilestone(dto.getIsMilestone());

        if (dto.getAssigneeId() != null) {
            User assignee = new User();
            assignee.setId(dto.getAssigneeId());
            task.setAssignee(assignee);
        }

        if (dto.getParentTaskId() != null) {
            Task parentTask = new Task();
            parentTask.setId(dto.getParentTaskId());
            task.setParentTask(parentTask);
        }
    }

    // ==================== LIST OPERATIONS ====================

    public List<TaskResponseDTO> toResponseDTOList(List<Task> tasks) {
        if (tasks == null) return null;
        return tasks.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TaskSummaryDTO> toSummaryDTOList(List<Task> tasks) {
        if (tasks == null) return null;
        return tasks.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    public List<TaskSummaryDTO> subtasksToSummaryList(Set<Task> subtasks) {
        if (subtasks == null) return null;
        return subtasks.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================

    private Integer calculateSubtaskCount(Set<Task> subtasks) {
        return subtasks != null ? subtasks.size() : 0;
    }

    private Integer calculateCompletedSubtaskCount(Set<Task> subtasks) {
        if (subtasks == null) return 0;
        return (int) subtasks.stream()
                .filter(task -> task.getStatus() == com.taskManagement.entity.TaskStatus.COMPLETED)
                .count();
    }

    private Integer calculateCommentCount(Set<?> comments) {
        return comments != null ? comments.size() : 0;
    }

    private Integer calculateAttachmentCount(Set<?> attachments) {
        return attachments != null ? attachments.size() : 0;
    }

    private Integer calculateDependencyCount(Set<?> dependencies) {
        return dependencies != null ? dependencies.size() : 0;
    }

    private Long calculateDaysUntilDue(LocalDateTime dueDate) {
        if (dueDate == null) return null;
        return ChronoUnit.DAYS.between(LocalDateTime.now(), dueDate);
    }

    private Boolean calculateIsOverdue(LocalDateTime dueDate) {
        if (dueDate == null) return false;
        return LocalDateTime.now().isAfter(dueDate);
    }

    private Double calculateTimeSpentPercentage(Task task) {
        if (task.getEstimatedHours() == null || task.getEstimatedHours() == 0) return 0.0;
        if (task.getActualHours() == null) return 0.0;
        return (task.getActualHours() / task.getEstimatedHours()) * 100.0;
    }

    // Helper methods for nested objects (simplified)
    private com.taskManagement.dto.project.ProjectSummaryDTO toProjectSummary(Project project) {
        if (project == null) return null;
        // You'll need to implement this based on your ProjectSummaryDTO
        return null; // TODO: Implement based on your ProjectSummaryDTO
    }

    private com.taskManagement.dto.user.UserSummaryDTO toUserSummary(User user) {
        if (user == null) return null;
        // You'll need to implement this based on your UserSummaryDTO
        return null; // TODO: Implement based on your UserSummaryDTO
    }


}
