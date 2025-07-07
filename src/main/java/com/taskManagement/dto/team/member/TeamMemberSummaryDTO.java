package com.taskManagement.dto.team.member;

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
public class TeamMemberSummaryDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userAvatarUrl;
    private TeamRole role;
    private Boolean isActive;
    private LocalDateTime joinedAt;

}
