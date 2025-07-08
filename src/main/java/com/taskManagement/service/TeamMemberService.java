package com.taskManagement.service;

import com.taskManagement.dto.team.member.*;
import com.taskManagement.entity.TeamMember;
import com.taskManagement.entity.TeamRole;

import java.util.List;
import java.util.Optional;

public interface TeamMemberService {
    
    // ==================== BASIC CRUD OPERATIONS ====================
    
    TeamMemberResponseDTO addMemberToTeam(TeamMemberCreateDTO createDTO);
    
    Optional<TeamMemberResponseDTO> getTeamMemberById(Long id);
    
    Optional<TeamMemberResponseDTO> getTeamMember(Long teamId, Long userId);
    
    List<TeamMemberResponseDTO> getAllTeamMembers();
    
    TeamMemberResponseDTO updateTeamMember(Long id, TeamMemberUpdateDTO updateDTO);
    
    void removeTeamMember(Long id);
    
    void deactivateTeamMember(Long id);
    
    void activateTeamMember(Long id);
    
    // ==================== TEAM-BASED QUERIES ====================
    
    List<TeamMemberSummaryDTO> getMembersByTeamId(Long teamId);
    
    List<TeamMemberSummaryDTO> getActiveMembersByTeamId(Long teamId);
    
    List<TeamMemberSummaryDTO> getMembersByTeamAndRole(Long teamId, TeamRole role);
    
    List<TeamMemberSummaryDTO> getTeamAdmins(Long teamId);
    
    List<TeamMemberSummaryDTO> getTeamManagers(Long teamId);
    
    List<TeamMemberSummaryDTO> getRecentlyJoinedMembers(Long teamId, int limit);
    
    // ==================== USER-BASED QUERIES ====================
    
    List<TeamMemberSummaryDTO> getMembershipsByUserId(Long userId);
    
    List<TeamMemberSummaryDTO> getActiveMembershipsByUserId(Long userId);
    
    List<TeamMemberSummaryDTO> getUserMembershipsByRole(Long userId, TeamRole role);
    
    // ==================== MEMBERSHIP VALIDATION ====================
    
    boolean isMemberOfTeam(Long teamId, Long userId);
    
    boolean isActiveMemberOfTeam(Long teamId, Long userId);
    
    boolean hasRole(Long teamId, Long userId, TeamRole role);
    
    boolean canUserJoinTeam(Long teamId, Long userId);
    
    boolean isTeamFull(Long teamId);
    
    // ==================== ROLE MANAGEMENT ====================
    
    TeamMemberResponseDTO updateMemberRole(Long teamId, Long userId, TeamRole newRole);
    
    TeamMemberResponseDTO promoteToAdmin(Long teamId, Long userId);
    
    TeamMemberResponseDTO promoteToManager(Long teamId, Long userId);
    
    TeamMemberResponseDTO demoteToMember(Long teamId, Long userId);
    
    // ==================== BULK OPERATIONS ====================
    
    List<TeamMemberResponseDTO> addMultipleMembers(Long teamId, List<Long> userIds, TeamRole role);
    
    void removeMultipleMembers(Long teamId, List<Long> userIds);
    
    void updateMultipleMemberRoles(Long teamId, List<Long> userIds, TeamRole newRole);
    
    // ==================== INVITATION MANAGEMENT ====================
    
    TeamMemberResponseDTO inviteUserToTeam(Long teamId, Long userId, Long invitedBy, TeamRole role);
    
    TeamMemberResponseDTO acceptInvitation(Long teamMemberId);
    
    void declineInvitation(Long teamMemberId);
    
    List<TeamMemberSummaryDTO> getPendingInvitations(Long teamId);
    
    List<TeamMemberSummaryDTO> getUserPendingInvitations(Long userId);
    
    // ==================== TEAM STATISTICS ====================
    
    Integer getTeamMemberCount(Long teamId);
    
    Integer getActiveMemberCount(Long teamId);
    
    Integer getMemberCountByRole(Long teamId, TeamRole role);
    
    TeamMemberStatsDTO getTeamMemberStatistics(Long teamId);
    
    // ==================== INTERNAL METHODS (Entity-based for other services) ====================
    
    TeamMember findTeamMemberEntityById(Long id);
    
    TeamMember findTeamMemberEntity(Long teamId, Long userId);
    
    List<TeamMember> findActiveTeamMemberEntities(Long teamId);
}