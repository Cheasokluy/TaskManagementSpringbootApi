package com.taskManagement.service;

import com.taskManagement.dto.team.*;
import com.taskManagement.dto.team.member.TeamMemberResponseDTO;
import com.taskManagement.dto.team.member.TeamMemberSummaryDTO;
import com.taskManagement.entity.Team;

import java.util.List;
import java.util.Optional;

public interface TeamService {
    
    // ==================== BASIC CRUD OPERATIONS ====================
    
    TeamResponseDTO createTeam(TeamCreateDTO teamCreateDTO);
    
    Optional<TeamResponseDTO> getTeamById(Long id);
    
    Optional<TeamResponseDTO> getTeamByCode(String teamCode);
    
    List<TeamResponseDTO> getAllTeams();
    
    List<TeamResponseDTO> getAllActiveTeams();
    
    List<TeamSummaryDTO> getAllTeamsSummary();
    
    TeamResponseDTO updateTeam(Long id, TeamUpdateDTO teamUpdateDTO);
    
    void deleteTeam(Long id);
    
    void deactivateTeam(Long id);
    
    void activateTeam(Long id);
    
    // ==================== TEAM QUERIES ====================
    
    List<TeamSummaryDTO> getTeamsByUserId(Long userId);
    
    List<TeamSummaryDTO> getActiveTeamsByUserId(Long userId);
    
    List<TeamSummaryDTO> getTeamsOrderByCreatedDate();
    
    List<TeamSummaryDTO> getTeamsByMemberCount(Integer minMembers, Integer maxMembers);
    
    List<TeamSummaryDTO> searchTeamsByName(String namePattern);
    
    // ==================== TEAM VALIDATION ====================
    
    boolean existsByTeamCode(String teamCode);
    
    boolean canUserAccessTeam(Long userId, Long teamId);
    
    boolean isTeamActive(Long teamId);
    
    boolean isTeamFull(Long teamId);
    
    boolean hasAvailableSlots(Long teamId);
    
    // ==================== TEAM CODE MANAGEMENT ====================
    
    String generateUniqueTeamCode();
    
    TeamResponseDTO updateTeamCode(Long teamId, String newTeamCode);
    
    // ==================== TEAM SETTINGS ====================
    
    TeamResponseDTO updateTeamSettings(Long id, String name, String description, String avatarUrl);
    
    TeamResponseDTO updateTeamAvatar(Long id, String avatarUrl);
    
    TeamResponseDTO updateTeamCapacity(Long id, Integer maxMembers);
    
    // ==================== TEAM MEMBER MANAGEMENT ====================
    
    List<TeamMemberSummaryDTO> getTeamMembers(Long teamId);
    
    List<TeamMemberSummaryDTO> getActiveTeamMembers(Long teamId);
    
    List<TeamMemberSummaryDTO> getTeamMembersByRole(Long teamId, String role);
    
    Integer getTeamMemberCount(Long teamId);
    
    Integer getActiveTeamMemberCount(Long teamId);
    
    // ==================== TEAM STATISTICS ====================
    
    long getTotalTeamCount();
    
    long getActiveTeamCount();
    
    List<TeamSummaryDTO> getRecentTeams(int limit);
    
    List<TeamSummaryDTO> getPopularTeams(int limit);
    
    TeamStatsDTO getTeamStatistics(Long teamId);
    
    // ==================== TEAM ARCHIVING ====================
    
    TeamResponseDTO archiveTeam(Long teamId);
    
    TeamResponseDTO unarchiveTeam(Long teamId);
    
    List<TeamSummaryDTO> getArchivedTeams();
    
    // ==================== INTERNAL METHODS (Entity-based for other services) ====================
    
    Team findTeamEntityById(Long id);
    
    Team findTeamEntityByCode(String teamCode);
}