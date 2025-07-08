package com.taskManagement.service.impl;

import com.taskManagement.dto.team.member.*;
import com.taskManagement.entity.Team;
import com.taskManagement.entity.TeamMember;
import com.taskManagement.entity.TeamRole;
import com.taskManagement.entity.User;
import com.taskManagement.exception.ResourceNotFoundException;
import com.taskManagement.exception.BusinessLogicException;
import com.taskManagement.mapper.TeamMemberMapper;
import com.taskManagement.repository.TeamMemberRepository;
import com.taskManagement.service.TeamMemberService;
import com.taskManagement.service.TeamService;
import com.taskManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamService teamService;
    private final UserService userService;

    // ==================== BASIC CRUD OPERATIONS ====================

    @Override
    public TeamMemberResponseDTO addMemberToTeam(TeamMemberCreateDTO createDTO) {
        log.info("Adding member to team - TeamID: {}, UserID: {}", createDTO.getTeamId(), createDTO.getUserId());
        
        // Validate team and user exist
        Team team = teamService.findTeamEntityById(createDTO.getTeamId());
        User user = userService.findUserEntityById(createDTO.getUserId());
        
        // Check if user is already a member
        if (isMemberOfTeam(createDTO.getTeamId(), createDTO.getUserId())) {
            throw new BusinessLogicException("User is already a member of this team");
        }
        
        // Check if team has available slots
        if (!canUserJoinTeam(createDTO.getTeamId(), createDTO.getUserId())) {
            throw new BusinessLogicException("Team is full or user cannot join");
        }
        
        // Create team member entity
        TeamMember teamMember = teamMemberMapper.toEntity(createDTO);
        teamMember.setTeam(team);
        teamMember.setUser(user);
        
        // Save team member
        TeamMember savedTeamMember = teamMemberRepository.save(teamMember);
        
        log.info("Successfully added member - ID: {}, TeamID: {}, UserID: {}", 
                savedTeamMember.getId(), createDTO.getTeamId(), createDTO.getUserId());
        
        return teamMemberMapper.toResponseDTO(savedTeamMember);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamMemberResponseDTO> getTeamMemberById(Long id) {
        log.debug("Fetching team member by ID: {}", id);
        
        return teamMemberRepository.findById(id)
                .map(teamMemberMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamMemberResponseDTO> getTeamMember(Long teamId, Long userId) {
        log.debug("Fetching team member - TeamID: {}, UserID: {}", teamId, userId);
        
        return teamMemberRepository.findByTeamIdAndUserIdAndIsActiveTrue(teamId, userId)
                .map(teamMemberMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberResponseDTO> getAllTeamMembers() {
        log.debug("Fetching all team members");
        
        List<TeamMember> members = teamMemberRepository.findAll();
        return teamMemberMapper.toResponseDTOList(members);
    }

    @Override
    public TeamMemberResponseDTO updateTeamMember(Long id, TeamMemberUpdateDTO updateDTO) {
        log.info("Updating team member with ID: {}", id);
        
        TeamMember teamMember = findTeamMemberEntityById(id);
        
        // Update using mapper
        teamMemberMapper.updateEntityFromDTO(teamMember, updateDTO);
        
        TeamMember updatedTeamMember = teamMemberRepository.save(teamMember);
        
        log.info("Successfully updated team member with ID: {}", id);
        return teamMemberMapper.toResponseDTO(updatedTeamMember);
    }

    @Override
    public void removeTeamMember(Long id) {
        log.info("Removing team member with ID: {}", id);
        
        TeamMember teamMember = findTeamMemberEntityById(id);
        
        // Check if this is the last admin
        if (teamMember.getRole() == TeamRole.ADMIN) {
            long adminCount = getMemberCountByRole(teamMember.getTeam().getId(), TeamRole.ADMIN);
            if (adminCount <= 1) {
                throw new BusinessLogicException("Cannot remove the last admin from the team");
            }
        }
        
        teamMemberRepository.delete(teamMember);
        
        log.info("Successfully removed team member with ID: {}", id);
    }

    @Override
    public void deactivateTeamMember(Long id) {
        log.info("Deactivating team member with ID: {}", id);
        
        TeamMember teamMember = findTeamMemberEntityById(id);
        
        // Check if this is the last admin
        if (teamMember.getRole() == TeamRole.ADMIN) {
            long activeAdminCount = getActiveMemberCountByRole(teamMember.getTeam().getId(), TeamRole.ADMIN);
            if (activeAdminCount <= 1) {
                throw new BusinessLogicException("Cannot deactivate the last active admin");
            }
        }
        
        teamMember.setIsActive(false);
        teamMemberRepository.save(teamMember);
        
        log.info("Successfully deactivated team member with ID: {}", id);
    }

    @Override
    public void activateTeamMember(Long id) {
        log.info("Activating team member with ID: {}", id);
        
        TeamMember teamMember = findTeamMemberEntityById(id);
        
        // Check if team has available slots
        if (!canUserJoinTeam(teamMember.getTeam().getId(), teamMember.getUser().getId())) {
            throw new BusinessLogicException("Team is full, cannot activate member");
        }
        
        teamMember.setIsActive(true);
        teamMemberRepository.save(teamMember);
        
        log.info("Successfully activated team member with ID: {}", id);
    }

    // ==================== TEAM-BASED QUERIES ====================

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getMembersByTeamId(Long teamId) {
        log.debug("Fetching all members for team ID: {}", teamId);
        
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        return teamMemberMapper.toSummaryDTOList(members);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getActiveMembersByTeamId(Long teamId) {
        log.debug("Fetching active members for team ID: {}", teamId);
        
        List<TeamMember> activeMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        return teamMemberMapper.toSummaryDTOList(activeMembers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getMembersByTeamAndRole(Long teamId, TeamRole role) {
        log.debug("Fetching members for team ID: {} with role: {}", teamId, role);
        
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndRoleAndIsActiveTrue(teamId, role);
        return teamMemberMapper.toSummaryDTOList(members);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getTeamAdmins(Long teamId) {
        return getMembersByTeamAndRole(teamId, TeamRole.ADMIN);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getTeamManagers(Long teamId) {
        return getMembersByTeamAndRole(teamId, TeamRole.MANAGER);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getRecentlyJoinedMembers(Long teamId, int limit) {
        log.debug("Fetching {} recently joined members for team ID: {}", limit, teamId);
        
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrueOrderByJoinedAtDesc(teamId);
        return members.stream()
                .limit(limit)
                .map(teamMemberMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // ==================== USER-BASED QUERIES ====================

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getMembershipsByUserId(Long userId) {
        log.debug("Fetching all memberships for user ID: {}", userId);
        
        List<TeamMember> memberships = teamMemberRepository.findByUserIdAndIsActiveTrue(userId);
        return teamMemberMapper.toSummaryDTOList(memberships);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getActiveMembershipsByUserId(Long userId) {
        log.debug("Fetching active memberships for user ID: {}", userId);
        
        List<TeamMember> activeMemberships = teamMemberRepository.findByUserIdAndIsActiveTrue(userId);
        return teamMemberMapper.toSummaryDTOList(activeMemberships);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getUserMembershipsByRole(Long userId, TeamRole role) {
        log.debug("Fetching memberships for user ID: {} with role: {}", userId, role);
        
        List<TeamMember> memberships = teamMemberRepository.findByUserIdAndIsActiveTrue(userId);
        return memberships.stream()
                .filter(member -> member.getRole() == role)
                .map(teamMemberMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // ==================== MEMBERSHIP VALIDATION ====================

    @Override
    @Transactional(readOnly = true)
    public boolean isMemberOfTeam(Long teamId, Long userId) {
        return teamMemberRepository.existsByTeamIdAndUserIdAndIsActiveTrue(teamId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isActiveMemberOfTeam(Long teamId, Long userId) {
        return teamMemberRepository.existsByTeamIdAndUserIdAndIsActiveTrue(teamId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long teamId, Long userId, TeamRole role) {
        return teamMemberRepository.findByTeamIdAndUserIdAndIsActiveTrue(teamId, userId)
                .map(member -> member.getRole() == role)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserJoinTeam(Long teamId, Long userId) {
        // Check if user is already a member
        if (isMemberOfTeam(teamId, userId)) {
            return false;
        }
        
        // Check if team is full
        if (isTeamFull(teamId)) {
            return false;
        }
        
        // Check if team is active
        if (!teamService.isTeamActive(teamId)) {
            return false;
        }
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTeamFull(Long teamId) {
        return teamService.isTeamFull(teamId);
    }

    // ==================== ROLE MANAGEMENT ====================

    @Override
    public TeamMemberResponseDTO updateMemberRole(Long teamId, Long userId, TeamRole newRole) {
        log.info("Updating member role - TeamID: {}, UserID: {}, NewRole: {}", teamId, userId, newRole);
        
        TeamMember teamMember = findTeamMemberEntity(teamId, userId);
        TeamRole oldRole = teamMember.getRole();
        
        // Check if demoting the last admin
        if (oldRole == TeamRole.ADMIN && newRole != TeamRole.ADMIN) {
            long adminCount = getMemberCountByRole(teamId, TeamRole.ADMIN);
            if (adminCount <= 1) {
                throw new BusinessLogicException("Cannot demote the last admin");
            }
        }
        
        teamMember.setRole(newRole);
        TeamMember updatedTeamMember = teamMemberRepository.save(teamMember);
        
        log.info("Successfully updated member role - ID: {}, OldRole: {}, NewRole: {}", 
                teamMember.getId(), oldRole, newRole);
        
        return teamMemberMapper.toResponseDTO(updatedTeamMember);
    }

    @Override
    public TeamMemberResponseDTO promoteToAdmin(Long teamId, Long userId) {
        return updateMemberRole(teamId, userId, TeamRole.ADMIN);
    }

    @Override
    public TeamMemberResponseDTO promoteToManager(Long teamId, Long userId) {
        return updateMemberRole(teamId, userId, TeamRole.MANAGER);
    }

    @Override
    public TeamMemberResponseDTO demoteToMember(Long teamId, Long userId) {
        return updateMemberRole(teamId, userId, TeamRole.MEMBER);
    }

    // ==================== BULK OPERATIONS ====================

    @Override
    public List<TeamMemberResponseDTO> addMultipleMembers(Long teamId, List<Long> userIds, TeamRole role) {
        log.info("Adding {} members to team ID: {} with role: {}", userIds.size(), teamId, role);
        
        return userIds.stream()
                .map(userId -> {
                    TeamMemberCreateDTO createDTO = TeamMemberCreateDTO.builder()
                            .teamId(teamId)
                            .userId(userId)
                            .role(role)
                            .build();
                    return addMemberToTeam(createDTO);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void removeMultipleMembers(Long teamId, List<Long> userIds) {
        log.info("Removing {} members from team ID: {}", userIds.size(), teamId);
        
        userIds.forEach(userId -> {
            TeamMember member = findTeamMemberEntity(teamId, userId);
            removeTeamMember(member.getId());
        });
    }

    @Override
    public void updateMultipleMemberRoles(Long teamId, List<Long> userIds, TeamRole newRole) {
        log.info("Updating role for {} members in team ID: {} to: {}", userIds.size(), teamId, newRole);
        
        userIds.forEach(userId -> updateMemberRole(teamId, userId, newRole));
    }

    // ==================== INVITATION MANAGEMENT ====================

    @Override
    public TeamMemberResponseDTO inviteUserToTeam(Long teamId, Long userId, Long invitedBy, TeamRole role) {
        log.info("Inviting user {} to team {} by user {} with role {}", userId, teamId, invitedBy, role);

        // Convert the invitedBy Long to String since DTO expects String
        Long invitedByString = invitedBy != null ? Long.valueOf(invitedBy.toString()) : null;

        TeamMemberCreateDTO createDTO = TeamMemberCreateDTO.builder()
                .teamId(teamId)
                .userId(userId)
                .role(role)
                .invitedBy(invitedByString)
                .build();

        return addMemberToTeam(createDTO);


    }

    @Override
    public TeamMemberResponseDTO acceptInvitation(Long teamMemberId) {
        log.info("Accepting invitation for team member ID: {}", teamMemberId);
        
        TeamMember teamMember = findTeamMemberEntityById(teamMemberId);
        teamMember.setIsActive(true);
        
        TeamMember updatedTeamMember = teamMemberRepository.save(teamMember);
        
        log.info("Successfully accepted invitation for team member ID: {}", teamMemberId);
        return teamMemberMapper.toResponseDTO(updatedTeamMember);
    }

    @Override
    public void declineInvitation(Long teamMemberId) {
        log.info("Declining invitation for team member ID: {}", teamMemberId);
        
        TeamMember teamMember = findTeamMemberEntityById(teamMemberId);
        teamMemberRepository.delete(teamMember);
        
        log.info("Successfully declined invitation for team member ID: {}", teamMemberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getPendingInvitations(Long teamId) {
        log.debug("Fetching pending invitations for team ID: {}", teamId);
        
        List<TeamMember> allMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        // TODO: Add invitation status field to TeamMember entity
        // For now, return empty list
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getUserPendingInvitations(Long userId) {
        log.debug("Fetching pending invitations for user ID: {}", userId);
        
        List<TeamMember> allMemberships = teamMemberRepository.findByUserIdAndIsActiveTrue(userId);
        // TODO: Add invitation status field to TeamMember entity
        // For now, return empty list
        return List.of();
    }

    // ==================== TEAM STATISTICS ====================

    @Override
    @Transactional(readOnly = true)
    public Integer getTeamMemberCount(Long teamId) {
        return teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getActiveMemberCount(Long teamId) {
        return teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getMemberCountByRole(Long teamId, TeamRole role) {
        return teamMemberRepository.findByTeamIdAndRoleAndIsActiveTrue(teamId, role).size();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamMemberStatsDTO getTeamMemberStatistics(Long teamId) {
        log.debug("Generating member statistics for team ID: {}", teamId);
        
        Team team = teamService.findTeamEntityById(teamId);
        List<TeamMember> allMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        
        // Count by role
        int adminCount = (int) allMembers.stream()
                .filter(member -> member.getRole() == TeamRole.ADMIN)
                .count();
        int managerCount = (int) allMembers.stream()
                .filter(member -> member.getRole() == TeamRole.MANAGER)
                .count();
        int memberCount = (int) allMembers.stream()
                .filter(member -> member.getRole() == TeamRole.MEMBER)
                .count();
        
        // Calculate activity metrics
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int recentJoinings = (int) allMembers.stream()
                .filter(member -> member.getJoinedAt().isAfter(thirtyDaysAgo))
                .count();
        
        // Get last activity
        LocalDateTime lastMemberJoined = allMembers.stream()
                .map(TeamMember::getJoinedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        
        // Calculate capacity metrics
        Integer maxCapacity = team.getMaxMembers();
        Integer currentCapacity = allMembers.size();
        Integer availableSlots = maxCapacity != null ? Math.max(0, maxCapacity - currentCapacity) : null;
        Double capacityUtilization = maxCapacity != null ? (double) currentCapacity / maxCapacity * 100 : null;
        
        return TeamMemberStatsDTO.builder()
                .teamId(teamId)
                .teamName(team.getName())
                .totalMembers(currentCapacity)
                .activeMembers(currentCapacity)
                .inactiveMembers(0)
                .pendingInvitations(0) // TODO: Implement when invitation system is enhanced
                .adminCount(adminCount)
                .managerCount(managerCount)
                .memberCount(memberCount)
                .guestCount(0) // TODO: Add guest role support
                .lastMemberJoined(lastMemberJoined)
                .lastMemberLeft(null) // TODO: Track member departure
                .recentJoinings(recentJoinings)
                .recentLeavings(0) // TODO: Track departures
                .memberRetentionRate(100.0) // TODO: Calculate based on historical data
                .averageMembershipDuration(0.0) // TODO: Calculate based on historical data
                .activeMembersLastWeek(currentCapacity) // TODO: Track activity
                .activeMembersLastMonth(currentCapacity) // TODO: Track activity
                .maxCapacity(maxCapacity)
                .currentCapacity(currentCapacity)
                .availableSlots(availableSlots)
                .capacityUtilization(capacityUtilization)
                .build();
    }

    // ==================== INTERNAL METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public TeamMember findTeamMemberEntityById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public TeamMember findTeamMemberEntity(Long teamId, Long userId) {
        return teamMemberRepository.findByTeamIdAndUserIdAndIsActiveTrue(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Team member not found - TeamID: %d, UserID: %d", teamId, userId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMember> findActiveTeamMemberEntities(Long teamId) {
        return teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
    }

    // ==================== HELPER METHODS ====================

    private long getActiveMemberCountByRole(Long teamId, TeamRole role) {
        return teamMemberRepository.findByTeamIdAndRoleAndIsActiveTrue(teamId, role).size();
    }
}