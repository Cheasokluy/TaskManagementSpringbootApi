package com.taskManagement.repository;

import com.taskManagement.entity.TaskDependency;
import com.taskManagement.entity.DependencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {
    List<TaskDependency> findByPrerequisiteTaskId(Long prerequisiteTaskId);

    List<TaskDependency> findByDependentTaskId(Long dependentTaskId);

    Optional<TaskDependency> findByPrerequisiteTaskIdAndDependentTaskId(Long prerequisiteTaskId, Long dependentTaskId);

    boolean existsByPrerequisiteTaskIdAndDependentTaskId(Long prerequisiteTaskId, Long dependentTaskId);

    List<TaskDependency> findByType(DependencyType type);

    List<TaskDependency> findByPrerequisiteTaskIdAndType(Long prerequisiteTaskId, DependencyType type);

    List<TaskDependency> findByDependentTaskIdAndType(Long dependentTaskId, DependencyType type);

}
