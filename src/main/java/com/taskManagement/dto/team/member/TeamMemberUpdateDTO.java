package com.taskManagement.dto.team.member;

import com.taskManagement.entity.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberUpdateDTO {

    private TeamRole role;
    private Boolean isActive;
}