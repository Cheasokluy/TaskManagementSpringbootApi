package com.taskManagement.repository;

import com.taskManagement.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByTeamCode(String teamCode);

    List<Team> findByIsActiveTrue();

    boolean existsByTeamCode(String teamCode);

    List<Team> findByIsActiveTrueOrderByCreatedAtDesc();


}
