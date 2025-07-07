package com.taskManagement.dto.team.member;

import com.taskManagement.dto.user.UserSummaryDTO;
import com.taskManagement.dto.team.TeamSummaryDTO;
import com.taskManagement.entity.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberResponseDTO {
    private Long id;
    private TeamRole role;
    private Boolean isActive;
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;

    // Related entities
    private UserSummaryDTO user;
    private TeamSummaryDTO team;

}
