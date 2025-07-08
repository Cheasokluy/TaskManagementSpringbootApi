package com.taskManagement.service.impl;

import com.taskManagement.dto.team.*;
import com.taskManagement.dto.team.member.TeamMemberSummaryDTO;
import com.taskManagement.entity.Team;
import com.taskManagement.entity.TeamMember;
import com.taskManagement.exception.ResourceNotFoundException;
import com.taskManagement.exception.BusinessLogicException;
import com.taskManagement.mapper.TeamMapper;
import com.taskManagement.repository.TeamRepository;
import com.taskManagement.repository.TeamMemberRepository;
import com.taskManagement.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMapper teamMapper;
    
    private static final String TEAM_CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TEAM_CODE_LENGTH = 8;
    private static final int MAX_GENERATION_ATTEMPTS = 10;

    // ==================== BASIC CRUD OPERATIONS ====================

    @Override
    public TeamResponseDTO createTeam(TeamCreateDTO teamCreateDTO) {
        log.info("Creating new team with name: {}", teamCreateDTO.getName());
        
        // Validate team code uniqueness
        if (teamCreateDTO.getTeamCode() != null && existsByTeamCode(teamCreateDTO.getTeamCode())) {
            throw new BusinessLogicException("Team code already exists: " + teamCreateDTO.getTeamCode());
        }
        
        // Convert DTO to entity
        Team team = teamMapper.toEntity(teamCreateDTO);
        
        // Generate unique team code if not provided
        if (team.getTeamCode() == null || team.getTeamCode().trim().isEmpty()) {
            team.setTeamCode(generateUniqueTeamCode());
        }
        
        // Save team
        Team savedTeam = teamRepository.save(team);
        
        log.info("Successfully created team with ID: {} and code: {}", savedTeam.getId(), savedTeam.getTeamCode());
        return teamMapper.toResponseDTO(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamResponseDTO> getTeamById(Long id) {
        log.debug("Fetching team by ID: {}", id);
        
        return teamRepository.findById(id)
                .map(teamMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamResponseDTO> getTeamByCode(String teamCode) {
        log.debug("Fetching team by code: {}", teamCode);
        
        return teamRepository.findByTeamCode(teamCode)
                .map(teamMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponseDTO> getAllTeams() {
        log.debug("Fetching all teams");
        
        List<Team> teams = teamRepository.findAll();
        return teamMapper.toResponseDTOList(teams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponseDTO> getAllActiveTeams() {
        log.debug("Fetching all active teams");
        
        List<Team> activeTeams = teamRepository.findByIsActiveTrue();
        return teamMapper.toResponseDTOList(activeTeams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryDTO> getAllTeamsSummary() {
        log.debug("Fetching all teams summary");
        
        List<Team> teams = teamRepository.findAll();
        return teamMapper.toSummaryDTOList(teams);
    }

    @Override
    public TeamResponseDTO updateTeam(Long id, TeamUpdateDTO teamUpdateDTO) {
        log.info("Updating team with ID: {}", id);
        
        Team team = findTeamEntityById(id);
        
        // Validate team code uniqueness if changed
        if (teamUpdateDTO.getTeamCode() != null && 
            !teamUpdateDTO.getTeamCode().equals(team.getTeamCode()) &&
            existsByTeamCode(teamUpdateDTO.getTeamCode())) {
            throw new BusinessLogicException("Team code already exists: " + teamUpdateDTO.getTeamCode());
        }
        
        // Update team using mapper
        teamMapper.updateEntityFromDTO(team, teamUpdateDTO);
        
        Team updatedTeam = teamRepository.save(team);
        
        log.info("Successfully updated team with ID: {}", updatedTeam.getId());
        return teamMapper.toResponseDTO(updatedTeam);
    }

    @Override
    public void deleteTeam(Long id) {
        log.info("Deleting team with ID: {}", id);
        
        Team team = findTeamEntityById(id);
        
        // Check if team has active members
        if (getActiveMemberCount(id) > 0) {
            throw new BusinessLogicException("Cannot delete team with active members");
        }
        
        teamRepository.delete(team);
        
        log.info("Successfully deleted team with ID: {}", id);
    }

    @Override
    public void deactivateTeam(Long id) {
        log.info("Deactivating team with ID: {}", id);
        
        Team team = findTeamEntityById(id);
        team.setIsActive(false);
        teamRepository.save(team);
        
        log.info("Successfully deactivated team with ID: {}", id);
    }

    @Override
    public void activateTeam(Long id) {
        log.info("Activating team with ID: {}", id);
        
        Team team = findTeamEntityById(id);
        team.setIsActive(true);
        teamRepository.save(team);
        
        log.info("Successfully activated team with ID: {}", id);
    }

    // ==================== TEAM QUERIES ====================

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryDTO> getTeamsByUserId(Long userId) {
        log.debug("Fetching teams for user ID: {}", userId);
        
        List<TeamMember> memberships = teamMemberRepository.findByUserIdAndIsActiveTrue(userId);
        
        return memberships.stream()
                .map(TeamMember::getTeam)
                .map(teamMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryDTO> getActiveTeamsByUserId(Long userId) {
        log.debug("Fetching active teams for user ID: {}", userId);
        
        List<TeamMember> memberships = teamMemberRepository.findByUserIdAndIsActiveTrue(userId);
        
        return memberships.stream()
                .map(TeamMember::getTeam)
                .filter(Team::getIsActive)
                .map(teamMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryDTO> getTeamsOrderByCreatedDate() {
        log.debug("Fetching teams ordered by created date");
        
        List<Team> teams = teamRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        return teamMapper.toSummaryDTOList(teams);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryDTO> getTeamsByMemberCount(Integer minMembers, Integer maxMembers) {
        log.debug("Fetching teams with member count between {} and {}", minMembers, maxMembers);
        
        List<Team> allTeams = teamRepository.findByIsActiveTrue();
        
        return allTeams.stream()
                .filter(team -> {
                    int memberCount = getActiveMemberCount(team.getId());
                    return (minMembers == null || memberCount >= minMembers) &&
                           (maxMembers == null || memberCount <= maxMembers);
                })
                .map(teamMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryDTO> searchTeamsByName(String namePattern) {
        log.debug("Searching teams by name pattern: {}", namePattern);
        
        List<Team> allTeams = teamRepository.findByIsActiveTrue();
        
        return allTeams.stream()
                .filter(team -> team.getName().toLowerCase().contains(namePattern.toLowerCase()))
                .map(teamMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // ==================== TEAM VALIDATION ====================

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTeamCode(String teamCode) {
        return teamRepository.existsByTeamCode(teamCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserAccessTeam(Long userId, Long teamId) {
        return teamMemberRepository.existsByTeamIdAndUserIdAndIsActiveTrue(teamId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTeamActive(Long teamId) {
        return teamRepository.findById(teamId)
                .map(Team::getIsActive)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTeamFull(Long teamId) {
        Team team = findTeamEntityById(teamId);
        
        if (team.getMaxMembers() == null) {
            return false; // No member limit
        }
        
        int currentMemberCount = getActiveMemberCount(teamId);
        return currentMemberCount >= team.getMaxMembers();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAvailableSlots(Long teamId) {
        return !isTeamFull(teamId);
    }

    // ==================== TEAM CODE MANAGEMENT ====================

    @Override
    public String generateUniqueTeamCode() {
        log.debug("Generating unique team code");
        
        SecureRandom random = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder(TEAM_CODE_LENGTH);
        
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            codeBuilder.setLength(0);
            
            // Generate random code
            for (int i = 0; i < TEAM_CODE_LENGTH; i++) {
                int randomIndex = random.nextInt(TEAM_CODE_CHARACTERS.length());
                codeBuilder.append(TEAM_CODE_CHARACTERS.charAt(randomIndex));
            }
            
            String generatedCode = codeBuilder.toString();
            
            // Check if code is unique
            if (!existsByTeamCode(generatedCode)) {
                log.debug("Generated unique team code: {}", generatedCode);
                return generatedCode;
            }
        }
        
        throw new BusinessLogicException("Unable to generate unique team code after " + MAX_GENERATION_ATTEMPTS + " attempts");
    }

    @Override
    public TeamResponseDTO updateTeamCode(Long teamId, String newTeamCode) {
        log.info("Updating team code for team ID: {} to: {}", teamId, newTeamCode);
        
        if (existsByTeamCode(newTeamCode)) {
            throw new BusinessLogicException("Team code already exists: " + newTeamCode);
        }
        
        Team team = findTeamEntityById(teamId);
        team.setTeamCode(newTeamCode);
        Team updatedTeam = teamRepository.save(team);
        
        log.info("Successfully updated team code for team ID: {}", teamId);
        return teamMapper.toResponseDTO(updatedTeam);
    }

    // ==================== TEAM SETTINGS ====================

    @Override
    public TeamResponseDTO updateTeamSettings(Long id, String name, String description, String avatarUrl) {
        log.info("Updating team settings for team ID: {}", id);
        
        Team team = findTeamEntityById(id);
        
        if (name != null && !name.trim().isEmpty()) {
            team.setName(name.trim());
        }
        if (description != null) {
            team.setDescription(description.trim());
        }
        if (avatarUrl != null) {
            team.setAvatarUrl(avatarUrl.trim());
        }
        
        Team updatedTeam = teamRepository.save(team);
        
        log.info("Successfully updated team settings for team ID: {}", id);
        return teamMapper.toResponseDTO(updatedTeam);
    }

    @Override
    public TeamResponseDTO updateTeamAvatar(Long id, String avatarUrl) {
        log.info("Updating team avatar for team ID: {}", id);
        
        Team team = findTeamEntityById(id);
        team.setAvatarUrl(avatarUrl);
        Team updatedTeam = teamRepository.save(team);
        
        log.info("Successfully updated team avatar for team ID: {}", id);
        return teamMapper.toResponseDTO(updatedTeam);
    }

    @Override
    public TeamResponseDTO updateTeamCapacity(Long id, Integer maxMembers) {
        log.info("Updating team capacity for team ID: {} to: {}", id, maxMembers);
        
        Team team = findTeamEntityById(id);
        
        // Validate that new capacity is not less than current member count
        if (maxMembers != null && maxMembers > 0) {
            int currentMemberCount = getActiveMemberCount(id);
            if (maxMembers < currentMemberCount) {
                throw new BusinessLogicException(
                    String.format("Cannot set capacity to %d. Current active members: %d", 
                                maxMembers, currentMemberCount));
            }
        }
        
        team.setMaxMembers(maxMembers);
        Team updatedTeam = teamRepository.save(team);
        
        log.info("Successfully updated team capacity for team ID: {}", id);
        return teamMapper.toResponseDTO(updatedTeam);
    }

    // ==================== TEAM MEMBER MANAGEMENT ====================

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getTeamMembers(Long teamId) {
        log.debug("Fetching all members for team ID: {}", teamId);
        
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        return members.stream()
                .map(teamMapper::toTeamMemberSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getActiveTeamMembers(Long teamId) {
        log.debug("Fetching active members for team ID: {}", teamId);
        
        List<TeamMember> activeMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        return activeMembers.stream()
                .map(teamMapper::toTeamMemberSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberSummaryDTO> getTeamMembersByRole(Long teamId, String role) {
        log.debug("Fetching members for team ID: {} with role: {}", teamId, role);
        
        try {
            com.taskManagement.entity.TeamRole teamRole = com.taskManagement.entity.TeamRole.valueOf(role.toUpperCase());
            List<TeamMember> members = teamMemberRepository.findByTeamIdAndRoleAndIsActiveTrue(teamId, teamRole);
            return members.stream()
                    .map(teamMapper::toTeamMemberSummaryDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BusinessLogicException("Invalid team role: " + role);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTeamMemberCount(Long teamId) {
        log.debug("Getting member count for team ID: {}", teamId);
        
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        return members.size();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getActiveTeamMemberCount(Long teamId) {
        return getActiveMemberCount(teamId);
    }

    // ==================== TEAM STATISTICS ====================

    @Override
    @Transactional(readOnly = true)
    public long getTotalTeamCount() {
        return teamRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveTeamCount() {
        return teamRepository.findByIsActiveTrue().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryDTO> getRecentTeams(int limit) {
        log.debug("Fetching {} recent teams", limit);
        
        List<Team> recentTeams = teamRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        return recentTeams.stream()
                .limit(limit)
                .map(teamMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryDTO> getPopularTeams(int limit) {
        log.debug("Fetching {} popular teams", limit);
        
        List<Team> allTeams = teamRepository.findByIsActiveTrue();
        
        return allTeams.stream()
                .sorted((t1, t2) -> Integer.compare(
                    getActiveMemberCount(t2.getId()), 
                    getActiveMemberCount(t1.getId())))
                .limit(limit)
                .map(teamMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeamStatsDTO getTeamStatistics(Long teamId) {
        log.debug("Generating statistics for team ID: {}", teamId);
        
        Team team = findTeamEntityById(teamId);
        
        // Get member statistics
        List<TeamMember> allMembers = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        int totalMembers = allMembers.size();
        
        // Count by role
        long adminCount = allMembers.stream()
                .filter(member -> member.getRole() == com.taskManagement.entity.TeamRole.ADMIN)
                .count();
        long managerCount = allMembers.stream()
                .filter(member -> member.getRole() == com.taskManagement.entity.TeamRole.MANAGER)
                .count();
        long memberCount = allMembers.stream()
                .filter(member -> member.getRole() == com.taskManagement.entity.TeamRole.MEMBER)
                .count();
        
        // Calculate capacity metrics
        Integer maxMembers = team.getMaxMembers();
        Integer availableSlots = maxMembers != null ? Math.max(0, maxMembers - totalMembers) : null;
        Double capacityUtilization = maxMembers != null ? (double) totalMembers / maxMembers * 100 : null;
        
        // Get recent activity
        LocalDateTime lastActivity = allMembers.stream()
                .map(TeamMember::getJoinedAt)
                .max(LocalDateTime::compareTo)
                .orElse(team.getCreatedAt());
        
        // Count recent joinings (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int recentJoinings = (int) allMembers.stream()
                .filter(member -> member.getJoinedAt().isAfter(thirtyDaysAgo))
                .count();
        
        return TeamStatsDTO.builder()
                .teamId(teamId)
                .teamName(team.getName())
                .totalMembers(totalMembers)
                .activeMembers(totalMembers) // All fetched members are active
                .inactiveMembers(0)
                .adminCount((int) adminCount)
                .managerCount((int) managerCount)
                .memberCount((int) memberCount)
                .totalProjects(0) // TODO: Implement when ProjectService is available
                .activeProjects(0)
                .completedProjects(0)
                .onHoldProjects(0)
                .totalTasks(0) // TODO: Implement when TaskService is available
                .completedTasks(0)
                .inProgressTasks(0)
                .pendingTasks(0)
                .lastActivity(lastActivity)
                .recentJoinings(recentJoinings)
                .membershipRetentionRate(100.0) // TODO: Calculate based on historical data
                .projectCompletionRate(0.0) // TODO: Implement when ProjectService is available
                .taskCompletionRate(0.0) // TODO: Implement when TaskService is available
                .averageProjectDuration(0.0) // TODO: Implement when ProjectService is available
                .maxMembers(maxMembers)
                .availableSlots(availableSlots)
                .capacityUtilization(capacityUtilization)
                .build();
    }

    // ==================== TEAM ARCHIVING ====================

    @Override
    public TeamResponseDTO archiveTeam(Long teamId) {
        log.info("Archiving team with ID: {}", teamId);
        
        Team team = findTeamEntityById(teamId);
        team.setIsActive(false);
        // TODO: Add archived flag when Team entity is enhanced
        Team archivedTeam = teamRepository.save(team);
        
        log.info("Successfully archived team with ID: {}", teamId);
        return teamMapper.toResponseDTO(archivedTeam);
    }

    @Override
    public TeamResponseDTO unarchiveTeam(Long teamId) {
        log.info("Unarchiving team with ID: {}", teamId);
        
        Team team = findTeamEntityById(teamId);
        team.setIsActive(true);
        Team unarchivedTeam = teamRepository.save(team);
        
        log.info("Successfully unarchived team with ID: {}", teamId);
        return teamMapper.toResponseDTO(unarchivedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryDTO> getArchivedTeams() {
        log.debug("Fetching archived teams");
        
        List<Team> allTeams = teamRepository.findAll();
        return allTeams.stream()
                .filter(team -> !team.getIsActive())
                .map(teamMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // ==================== INTERNAL METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public Team findTeamEntityById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Team findTeamEntityByCode(String teamCode) {
        return teamRepository.findByTeamCode(teamCode)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with code: " + teamCode));
    }

    // ==================== HELPER METHODS ====================

    private int getActiveMemberCount(Long teamId) {
        return teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId).size();
    }
}