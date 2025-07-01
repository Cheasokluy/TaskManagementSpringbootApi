package com.taskManagement.service;

import com.taskManagement.entity.TeamMember;
import com.taskManagement.entity.TeamRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeamMemberService {
    // Basic CRUD operations
    TeamMember createTeamMember(TeamMember teamMember);

    Optional<TeamMember> getTeamMemberById(Long id);

    Optional<TeamMember> getTeamMemberByTeamAndUser(Long teamId, Long userId);

    List<TeamMember> getAllTeamMembers();

    TeamMember updateTeamMember(Long id, TeamMember teamMember);

    void deleteTeamMember(Long id);

    // Team membership management
    TeamMember addUserToTeam(Long teamId, Long userId, TeamRole role);

    TeamMember addUserToTeamWithInvitation(Long teamId, Long userId, TeamRole role, Long invitedById);

    void removeUserFromTeam(Long teamId, Long userId);

    void deactivateTeamMember(Long teamId, Long userId);

    void reactivateTeamMember(Long teamId, Long userId);

    // Role management
    TeamMember updateMemberRole(Long teamId, Long userId, TeamRole newRole);

    TeamMember promoteMember(Long teamId, Long userId);

    TeamMember demoteMember(Long teamId, Long userId);

    // Query by team
    List<TeamMember> getMembersByTeamId(Long teamId);

    List<TeamMember> getActiveMembersByTeamId(Long teamId);

    List<TeamMember> getMembersByTeamIdOrderByJoinDate(Long teamId);

    List<TeamMember> getMembersByTeamIdAndRole(Long teamId, TeamRole role);

    // Query by user
    List<TeamMember> getMembershipsByUserId(Long userId);

    List<TeamMember> getActiveMembershipsByUserId(Long userId);

    List<TeamMember> getMembershipsByUserIdOrderByJoinDate(Long userId);

    // Query by role
    List<TeamMember> getTeamLeaders(Long teamId);

    List<TeamMember> getTeamManagers(Long teamId);

    List<TeamMember> getRegularMembers(Long teamId);

    List<TeamMember> getAllMembersByRole(TeamRole role);

    // Membership validation
    boolean isUserMemberOfTeam(Long userId, Long teamId);

    boolean isActiveMemberOfTeam(Long userId, Long teamId);

    boolean hasRoleInTeam(Long userId, Long teamId, TeamRole role);

    boolean hasMinimumRole(Long userId, Long teamId, TeamRole minimumRole);

    // Permission checks
    boolean canManageTeam(Long userId, Long teamId);

    boolean canInviteMembers(Long userId, Long teamId);

    boolean canRemoveMembers(Long userId, Long teamId);

    boolean canUpdateMemberRole(Long userId, Long teamId, Long targetUserId, TeamRole newRole);

    // Team statistics
    long countMembersByTeamId(Long teamId);

    long countActiveMembersByTeamId(Long teamId);

    long countMembersByRole(Long teamId, TeamRole role);

    double getAverageTeamTenure(Long teamId);

    // Membership history
    List<TeamMember> getMembershipHistory(Long userId);

    List<TeamMember> getTeamMembershipHistory(Long teamId);

    List<TeamMember> getMembersJoinedAfter(Long teamId, LocalDateTime date);

    List<TeamMember> getMembersJoinedBefore(Long teamId, LocalDateTime date);

    // Bulk operations
    List<TeamMember> addMultipleUsersToTeam(Long teamId, List<Long> userIds, TeamRole role);

    void removeMultipleUsersFromTeam(Long teamId, List<Long> userIds);

    void updateMultipleMemberRoles(Long teamId, List<Long> userIds, TeamRole newRole);

    // Team transfer and ownership
    TeamMember transferTeamOwnership(Long teamId, Long currentOwnerId, Long newOwnerId);

    List<TeamMember> getPendingInvitations(Long teamId);

    TeamMember acceptInvitation(Long teamMemberId);

    void declineInvitation(Long teamMemberId);

    // Member activity tracking
    TeamMember updateLastActiveTime(Long teamId, Long userId);

    List<TeamMember> getInactiveMembers(Long teamId, int daysInactive);

    List<TeamMember> getActiveMembersInPeriod(Long teamId, LocalDateTime start, LocalDateTime end);

    // Team hierarchy
    List<TeamMember> getSubordinates(Long teamId, Long managerId);

    Optional<TeamMember> getDirectManager(Long teamId, Long userId);

    List<TeamMember> getTeamHierarchy(Long teamId);

}
