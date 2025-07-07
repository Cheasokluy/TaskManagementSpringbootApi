package com.taskManagement.dto.team.member;

import com.taskManagement.entity.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TeamMemberCreateDTO {
    @NotNull(message = "Team ID is required")
    private Long teamId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Team role is required")
    private TeamRole role;

    @Builder.Default
    private Boolean isActive = true;

}
