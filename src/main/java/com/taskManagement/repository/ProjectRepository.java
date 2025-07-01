package com.taskManagement.repository;

import com.taskManagement.entity.Project;
import com.taskManagement.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTeamId(Long teamId);

    List<Project> findByTeamIdAndStatus(Long teamId, ProjectStatus status);

    List<Project> findByProjectManagerId(Long managerId);

    List<Project> findByTeamIdOrderByCreatedAtDesc(Long teamId);

    long countByTeamIdAndStatus(Long teamId, ProjectStatus status);

    List<Project> findByTeamIdAndStatusOrderByCreatedAtDesc(Long teamId, ProjectStatus status);

}
