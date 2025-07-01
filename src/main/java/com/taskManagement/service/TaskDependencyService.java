package com.taskManagement.service;

import com.taskManagement.entity.TaskDependency;
import com.taskManagement.entity.DependencyType;

import java.util.List;
import java.util.Optional;

public interface TaskDependencyService {
    // Basic CRUD operations
    TaskDependency createDependency(TaskDependency taskDependency);

    Optional<TaskDependency> getDependencyById(Long id);

    List<TaskDependency> getAllDependencies();

    void deleteDependency(Long id);

    // Dependency management
    TaskDependency addTaskDependency(Long dependentTaskId, Long prerequisiteTaskId, DependencyType type);

    void removeTaskDependency(Long dependentTaskId, Long prerequisiteTaskId);

    boolean dependencyExists(Long prerequisiteTaskId, Long dependentTaskId);

    // Get dependencies
    List<TaskDependency> getPrerequisitesForTask(Long taskId);

    List<TaskDependency> getDependentsForTask(Long taskId);

    List<TaskDependency> getDependenciesByType(DependencyType type);

    List<TaskDependency> getPrerequisitesByType(Long taskId, DependencyType type);

    List<TaskDependency> getDependentsByType(Long taskId, DependencyType type);

    // Dependency validation
    boolean canTaskBeCompleted(Long taskId);

    boolean wouldCreateCircularDependency(Long dependentTaskId, Long prerequisiteTaskId);

    List<Long> getBlockedTasks(Long taskId);

    List<Long> getBlockingTasks(Long taskId);

    // Dependency analysis
    List<Long> getTaskExecutionOrder(Long projectId);

    boolean hasUnresolvedDependencies(Long taskId);

}
