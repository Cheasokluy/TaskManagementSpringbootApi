package com.taskManagement.repository;
import com.taskManagement.entity.TeamMember;
import com.taskManagement.entity.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTeamIdAndIsActiveTrue(Long teamId);

    List<TeamMember> findByUserIdAndIsActiveTrue(Long userId);

    Optional<TeamMember> findByTeamIdAndUserIdAndIsActiveTrue(Long teamId, Long userId);

    List<TeamMember> findByTeamIdAndRoleAndIsActiveTrue(Long teamId, TeamRole role);

    boolean existsByTeamIdAndUserIdAndIsActiveTrue(Long teamId, Long userId);

    List<TeamMember> findByTeamIdAndIsActiveTrueOrderByJoinedAtDesc(Long teamId);

}
