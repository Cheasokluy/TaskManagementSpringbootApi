package com.taskManagement.mapper;

import com.taskManagement.dto.team.TeamSummaryDTO;
import com.taskManagement.dto.team.member.*;
import com.taskManagement.dto.user.UserSummaryDTO;
import com.taskManagement.entity.Team;
import com.taskManagement.entity.TeamMember;
import com.taskManagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamMemberMapper {

    @Autowired
    private TeamMapper teamMapper;

    // ==================== CREATE OPERATIONS ====================

    public TeamMember toEntity(TeamMemberCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        TeamMember teamMember = new TeamMember();
        teamMember.setRole(dto.getRole());
        teamMember.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        // Set relationships (IDs only for now, will be populated in service)
        if (dto.getTeamId() != null) {
            Team team = new Team();
            team.setId(dto.getTeamId());
            teamMember.setTeam(team);
        }

        if (dto.getUserId() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            teamMember.setUser(user);
        }

        return teamMember;
    }

    // ==================== READ OPERATIONS ====================

    public TeamMemberResponseDTO toResponseDTO(TeamMember teamMember) {
        if (teamMember == null) {
            return null;
        }

        return TeamMemberResponseDTO.builder()
                .id(teamMember.getId())
                .role(teamMember.getRole())
                .isActive(teamMember.getIsActive())
                .joinedAt(teamMember.getJoinedAt())
                .updatedAt(teamMember.getUpdatedAt())
                .user(toUserSummaryDTO(teamMember.getUser()))
                .team(teamMapper.toTeamSummaryForMember(teamMember.getTeam()))
                .build();
    }

    public TeamMemberSummaryDTO toSummaryDTO(TeamMember teamMember) {
        if (teamMember == null || teamMember.getUser() == null) {
            return null;
        }

        User user = teamMember.getUser();
        return TeamMemberSummaryDTO.builder()
                .id(teamMember.getId())
                .userId(user.getId())
                .userName(user.getUsername())
                .userEmail(user.getEmail())
                .userAvatarUrl(user.getProfilePictureUrl())
                .role(teamMember.getRole())
                .isActive(teamMember.getIsActive())
                .joinedAt(teamMember.getJoinedAt())
                .build();
    }

    // ==================== UPDATE OPERATIONS ====================

    public void updateEntityFromDTO(TeamMember teamMember, TeamMemberUpdateDTO dto) {
        if (dto == null || teamMember == null) {
            return;
        }

        if (dto.getRole() != null) {
            teamMember.setRole(dto.getRole());
        }
        if (dto.getIsActive() != null) {
            teamMember.setIsActive(dto.getIsActive());
        }

    }

    // ==================== LIST OPERATIONS ====================

    public List<TeamMemberResponseDTO> toResponseDTOList(List<TeamMember> teamMembers) {
        if (teamMembers == null) {
            return null;
        }
        return teamMembers.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TeamMemberSummaryDTO> toSummaryDTOList(List<TeamMember> teamMembers) {
        if (teamMembers == null) {
            return null;
        }
        return teamMembers.stream()
                .map(this::toSummaryDTO)
                .filter(dto -> dto != null) // Filter out null entries
                .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================

    private UserSummaryDTO toUserSummaryDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserSummaryDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }

    // ==================== SPECIALIZED CONVERSIONS ====================

    /**
     * Convert TeamMember to summary for team listings
     */
    public TeamMemberSummaryDTO toSummaryForTeam(TeamMember teamMember) {
        if (teamMember == null || teamMember.getUser() == null) {
            return null;
        }

        User user = teamMember.getUser();
        return TeamMemberSummaryDTO.builder()
                .id(teamMember.getId())
                .userId(user.getId())
                .userName(user.getUsername())
                .userEmail(user.getEmail())
                .userAvatarUrl(user.getProfilePictureUrl())
                .role(teamMember.getRole())
                .isActive(teamMember.getIsActive())
                .joinedAt(teamMember.getJoinedAt())
                .build();
    }

    /**
     * Convert TeamMember to response for user context (without user details)
     */
    public TeamMemberResponseDTO toResponseForUser(TeamMember teamMember) {
        if (teamMember == null) {
            return null;
        }

        return TeamMemberResponseDTO.builder()
                .id(teamMember.getId())
                .role(teamMember.getRole())
                .isActive(teamMember.getIsActive())
                .joinedAt(teamMember.getJoinedAt())
                .updatedAt(teamMember.getUpdatedAt())
                .team(teamMapper.toTeamSummaryForMember(teamMember.getTeam()))
                // Note: User details excluded for user context
                .build();
    }

    /**
     * Filter and convert active team members only
     */
    public List<TeamMemberSummaryDTO> toActiveMembersSummaryList(List<TeamMember> teamMembers) {
        if (teamMembers == null) {
            return null;
        }
        return teamMembers.stream()
                .filter(member -> member.getIsActive() && member.getUser() != null)
                .map(this::toSummaryDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Filter and convert team members by role
     */
    public List<TeamMemberSummaryDTO> toMembersByRoleSummaryList(List<TeamMember> teamMembers, 
                                                                com.taskManagement.entity.TeamRole role) {
        if (teamMembers == null || role == null) {
            return null;
        }
        return teamMembers.stream()
                .filter(member -> member.getRole() == role && 
                                member.getIsActive() && 
                                member.getUser() != null)
                .map(this::toSummaryDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Convert basic TeamMember to summary (minimal data)
     */
    public TeamMemberSummaryDTO toBasicSummaryDTO(TeamMember teamMember) {
        if (teamMember == null) {
            return null;
        }

        return TeamMemberSummaryDTO.builder()
                .id(teamMember.getId())
                .userId(teamMember.getUser() != null ? teamMember.getUser().getId() : null)
                .userName(teamMember.getUser() != null ? teamMember.getUser().getUsername() : "Unknown")
                .role(teamMember.getRole())
                .isActive(teamMember.getIsActive())
                .joinedAt(teamMember.getJoinedAt())
                .build();
    }

    /**
     * Bulk conversion with null safety
     */
    public List<TeamMemberSummaryDTO> toSummaryDTOListSafe(List<TeamMember> teamMembers) {
        if (teamMembers == null || teamMembers.isEmpty()) {
            return List.of(); // Return empty list instead of null
        }
        return teamMembers.stream()
                .filter(member -> member != null && member.getUser() != null)
                .map(this::toSummaryDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}