package com.taskManagement.service;

import com.taskManagement.entity.Team;
import com.taskManagement.entity.TeamMember;
import com.taskManagement.entity.TeamRole;

import java.util.List;
import java.util.Optional;

public interface TeamService {
    // Basic CRUD operations
    Team createTeam(Team team);

    Optional<Team> getTeamById(Long id);

    Optional<Team> getTeamByCode(String teamCode);

    List<Team> getAllTeams();

    List<Team> getAllActiveTeams();

    Team updateTeam(Long id, Team team);

    void deleteTeam(Long id);

    void deactivateTeam(Long id);

    void activateTeam(Long id);

    // Team queries
    List<Team> getTeamsByUserId(Long userId);

    List<Team> getActiveTeamsByUserId(Long userId);

    List<Team> getTeamsOrderByCreatedDate();

    // Team validation
    boolean existsByTeamCode(String teamCode);

    boolean canUserAccessTeam(Long userId, Long teamId);

    boolean isTeamActive(Long teamId);

    // Team code management
    String generateUniqueTeamCode();

    Team updateTeamCode(Long teamId, String newTeamCode);

    // Team settings
    Team updateTeamSettings(Long id, String name, String description, String colorCode);

    Team updateTeamAvatar(Long id, String avatarUrl);

    // Team statistics
    long getTotalTeamCount();

    long getActiveTeamCount();

    List<Team> getRecentTeams(int limit);

    List<Team> getPopularTeams(int limit);

    // Team archiving
    Team archiveTeam(Long teamId);

    Team unarchiveTeam(Long teamId);

    List<Team> getArchivedTeams();


}
