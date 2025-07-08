package com.taskManagement.controller;

import com.taskManagement.dto.common.ApiResponse;
import com.taskManagement.dto.team.*;
import com.taskManagement.dto.team.member.*;
import com.taskManagement.entity.TeamRole;
import com.taskManagement.service.TeamService;
import com.taskManagement.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Validated
public class TeamController {

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;

    // ==================== BASIC CRUD OPERATIONS ====================

    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponseDTO>> createTeam(@Valid @RequestBody TeamCreateDTO teamCreateDTO) {
        log.info("Creating team: {}", teamCreateDTO.getName());
        try {
            TeamResponseDTO createdTeam = teamService.createTeam(teamCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdTeam, "Team created successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error creating team: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating team: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create team: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponseDTO>> getTeamById(@PathVariable Long id) {
        log.info("Getting team by ID: {}", id);
        try {
            Optional<TeamResponseDTO> team = teamService.getTeamById(id);
            if (team.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(team.get(), "Team found"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Team not found with ID: " + id));
            }
        } catch (Exception e) {
            log.error("Error getting team by ID: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get team: " + e.getMessage()));
        }
    }

    @GetMapping("/code/{teamCode}")
    public ResponseEntity<ApiResponse<TeamResponseDTO>> getTeamByCode(@PathVariable String teamCode) {
        log.info("Getting team by code: {}", teamCode);
        try {
            Optional<TeamResponseDTO> team = teamService.getTeamByCode(teamCode);
            if (team.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(team.get(), "Team found"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Team not found with code: " + teamCode));
            }
        } catch (Exception e) {
            log.error("Error getting team by code: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get team: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamResponseDTO>>> getAllTeams() {
        log.info("Getting all teams");
        try {
            List<TeamResponseDTO> teams = teamService.getAllTeams();
            return ResponseEntity.ok(ApiResponse.success(teams, teams.size()));
        } catch (Exception e) {
            log.error("Error getting all teams: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get teams: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<TeamSummaryDTO>>> getAllTeamsSummary() {
        log.info("Getting all teams summary");
        try {
            List<TeamSummaryDTO> teams = teamService.getAllTeamsSummary();
            return ResponseEntity.ok(ApiResponse.success(teams, teams.size()));
        } catch (Exception e) {
            log.error("Error getting teams summary: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get teams summary: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponseDTO>> updateTeam(@PathVariable Long id,
                                                                   @Valid @RequestBody TeamUpdateDTO teamUpdateDTO) {
        log.info("Updating team with ID: {}", id);
        try {
            TeamResponseDTO updatedTeam = teamService.updateTeam(id, teamUpdateDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedTeam, "Team updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating team: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating team: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update team: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTeam(@PathVariable Long id) {
        log.info("Deleting team with ID: {}", id);
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.ok(ApiResponse.success("Team deleted", "Team deleted successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error deleting team: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deleting team: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete team: " + e.getMessage()));
        }
    }

    // ==================== TEAM MEMBER MANAGEMENT ====================

    @PostMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<TeamMemberResponseDTO>> addMemberToTeam(@PathVariable Long teamId,
                                                                              @Valid @RequestBody TeamMemberCreateDTO createDTO) {
        log.info("Adding member to team: {}", teamId);
        try {
            createDTO.setTeamId(teamId); // Ensure teamId is set
            TeamMemberResponseDTO member = teamMemberService.addMemberToTeam(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(member, "Member added to team successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error adding member to team: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error adding member to team: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to add member to team: " + e.getMessage()));
        }
    }

    @PostMapping("/{teamId}/members/invite")
    public ResponseEntity<ApiResponse<TeamMemberResponseDTO>> inviteUserToTeam(@PathVariable Long teamId,
                                                                               @RequestBody TeamInviteRequest request) {
        log.info("Inviting user {} to team {} with role {}", request.getUserId(), teamId, request.getRole());
        try {
            TeamMemberResponseDTO member = teamMemberService.inviteUserToTeam(
                    teamId, request.getUserId(), request.getInvitedBy(), request.getRole()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(member, "User invited to team successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error inviting user to team: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error inviting user to team: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to invite user to team: " + e.getMessage()));
        }
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<List<TeamMemberSummaryDTO>>> getTeamMembers(@PathVariable Long teamId) {
        log.info("Getting members for team: {}", teamId);
        try {
            List<TeamMemberSummaryDTO> members = teamService.getTeamMembers(teamId);
            return ResponseEntity.ok(ApiResponse.success(members, members.size()));
        } catch (Exception e) {
            log.error("Error getting team members: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get team members: " + e.getMessage()));
        }
    }

    @PutMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<ApiResponse<TeamMemberResponseDTO>> updateTeamMember(@PathVariable Long teamId,
                                                                               @PathVariable Long memberId,
                                                                               @Valid @RequestBody TeamMemberUpdateDTO updateDTO) {
        log.info("Updating team member {} for team {}", memberId, teamId);
        try {
            TeamMemberResponseDTO member = teamMemberService.updateTeamMember(memberId, updateDTO);
            return ResponseEntity.ok(ApiResponse.success(member, "Team member updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating team member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating team member: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update team member: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<ApiResponse<String>> removeMemberFromTeam(@PathVariable Long teamId,
                                                                    @PathVariable Long memberId) {
        log.info("Removing member {} from team {}", memberId, teamId);
        try {
            teamMemberService.removeTeamMember(memberId);
            return ResponseEntity.ok(ApiResponse.success("Member removed", "Member removed from team successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error removing member from team: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error removing member from team: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to remove member from team: " + e.getMessage()));
        }
    }

    // ==================== FILTER & SEARCH OPERATIONS ====================

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TeamSummaryDTO>>> getTeamsByUser(@PathVariable Long userId) {
        log.info("Getting teams for user: {}", userId);
        try {
            List<TeamSummaryDTO> teams = teamService.getTeamsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(teams, teams.size()));
        } catch (Exception e) {
            log.error("Error getting teams by user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get teams by user: " + e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TeamResponseDTO>>> getActiveTeams() {
        log.info("Getting active teams");
        try {
            //  FIXED: Use getAllActiveTeams instead of getActiveTeams
            List<TeamResponseDTO> teams = teamService.getAllActiveTeams();
            return ResponseEntity.ok(ApiResponse.success(teams, teams.size()));
        } catch (Exception e) {
            log.error("Error getting active teams: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get active teams: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TeamSummaryDTO>>> searchTeamsByName(@RequestParam String name) {
        log.info("Searching teams by name: {}", name);
        try {
            List<TeamSummaryDTO> teams = teamService.searchTeamsByName(name);
            return ResponseEntity.ok(ApiResponse.success(teams, teams.size()));
        } catch (Exception e) {
            log.error("Error searching teams by name: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search teams: " + e.getMessage()));
        }
    }

    // ==================== ADDITIONAL TEAM MEMBER ENDPOINTS ====================

    @GetMapping("/{teamId}/members/active")
    public ResponseEntity<ApiResponse<List<TeamMemberSummaryDTO>>> getActiveTeamMembers(@PathVariable Long teamId) {
        log.info("Getting active members for team: {}", teamId);
        try {
            List<TeamMemberSummaryDTO> members = teamService.getActiveTeamMembers(teamId);
            return ResponseEntity.ok(ApiResponse.success(members, members.size()));
        } catch (Exception e) {
            log.error("Error getting active team members: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get active team members: " + e.getMessage()));
        }
    }

    @GetMapping("/{teamId}/members/role/{role}")
    public ResponseEntity<ApiResponse<List<TeamMemberSummaryDTO>>> getMembersByRole(@PathVariable Long teamId, 
                                                                                    @PathVariable String role) {
        log.info("Getting members with role {} for team: {}", role, teamId);
        try {
            List<TeamMemberSummaryDTO> members = teamService.getTeamMembersByRole(teamId, role);
            return ResponseEntity.ok(ApiResponse.success(members, members.size()));
        } catch (Exception e) {
            log.error("Error getting members by role: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get members by role: " + e.getMessage()));
        }
    }

    @GetMapping("/{teamId}/stats")
    public ResponseEntity<ApiResponse<TeamStatsDTO>> getTeamStats(@PathVariable Long teamId) {
        log.info("Getting statistics for team: {}", teamId);
        try {
            //  FIXED: Use TeamService.getTeamStatistics
            TeamStatsDTO stats = teamService.getTeamStatistics(teamId);
            return ResponseEntity.ok(ApiResponse.success(stats, "Team statistics retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting team statistics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get team statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/{teamId}/members/count")
    public ResponseEntity<ApiResponse<Integer>> getTeamMemberCount(@PathVariable Long teamId) {
        log.info("Getting member count for team: {}", teamId);
        try {
            Integer count = teamService.getTeamMemberCount(teamId);
            return ResponseEntity.ok(ApiResponse.success(count, "Team member count retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting team member count: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get team member count: " + e.getMessage()));
        }
    }

    @PatchMapping("/{teamId}/members/{memberId}/role")
    public ResponseEntity<ApiResponse<TeamMemberResponseDTO>> updateMemberRole(@PathVariable Long teamId,
                                                                               @PathVariable Long memberId,
                                                                               @RequestBody RoleUpdateRequest request) {
        log.info("Updating role for member {} in team {} to {}", memberId, teamId, request.getRole());
        try {
            Optional<TeamMemberResponseDTO> memberOpt = teamMemberService.getTeamMemberById(memberId);
            if (memberOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Team member not found"));
            }
            
            Long userId = memberOpt.get().getUser().getId();
            TeamMemberResponseDTO member = teamMemberService.updateMemberRole(teamId, userId, request.getRole());
            return ResponseEntity.ok(ApiResponse.success(member, "Member role updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating member role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating member role: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update member role: " + e.getMessage()));
        }
    }

    // ==================== TEAM SETTINGS ====================

    @PatchMapping("/{teamId}/avatar")
    public ResponseEntity<ApiResponse<TeamResponseDTO>> updateTeamAvatar(@PathVariable Long teamId,
                                                                         @RequestBody AvatarUpdateRequest request) {
        log.info("Updating avatar for team: {}", teamId);
        try {
            TeamResponseDTO team = teamService.updateTeamAvatar(teamId, request.getAvatarUrl());
            return ResponseEntity.ok(ApiResponse.success(team, "Team avatar updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating team avatar: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating team avatar: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update team avatar: " + e.getMessage()));
        }
    }

    @PatchMapping("/{teamId}/capacity")
    public ResponseEntity<ApiResponse<TeamResponseDTO>> updateTeamCapacity(@PathVariable Long teamId,
                                                                           @RequestBody CapacityUpdateRequest request) {
        log.info("Updating capacity for team {} to {}", teamId, request.getMaxMembers());
        try {
            TeamResponseDTO team = teamService.updateTeamCapacity(teamId, request.getMaxMembers());
            return ResponseEntity.ok(ApiResponse.success(team, "Team capacity updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error updating team capacity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating team capacity: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update team capacity: " + e.getMessage()));
        }
    }

    // ==================== REQUEST CLASSES ====================

    public static class TeamInviteRequest {
        private Long userId;
        private Long invitedBy;
        private TeamRole role;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getInvitedBy() { return invitedBy; }
        public void setInvitedBy(Long invitedBy) { this.invitedBy = invitedBy; }
        public TeamRole getRole() { return role; }
        public void setRole(TeamRole role) { this.role = role; }
    }

    public static class RoleUpdateRequest {
        private TeamRole role;

        public TeamRole getRole() { return role; }
        public void setRole(TeamRole role) { this.role = role; }
    }

    public static class AvatarUpdateRequest {
        private String avatarUrl;

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }

    public static class CapacityUpdateRequest {
        private Integer maxMembers;

        public Integer getMaxMembers() { return maxMembers; }
        public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    }
}