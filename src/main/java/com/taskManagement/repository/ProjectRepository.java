package com.taskManagement.repository;

import com.taskManagement.entity.Priority;
import com.taskManagement.entity.Project;
import com.taskManagement.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Name validation
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);

    // Filter queries
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findByPriority(Priority priority);
    List<Project> findByTeamId(Long teamId);
    List<Project> findByProjectManagerId(Long managerId);
    List<Project> findByStatusIn(List<ProjectStatus> statuses);

    // Custom queries
    @Query("SELECT p FROM Project p WHERE p.endDate < :currentDate AND p.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Project> findOverdueProjects(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT p FROM Project p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Project> searchByKeyword(@Param("keyword") String keyword);


}
