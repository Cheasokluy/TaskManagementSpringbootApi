package com.taskManagement.dto.team.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberStatsDTO {
    
    private Long teamId;
    private String teamName;
    
    // Member counts by status
    private Integer totalMembers;
    private Integer activeMembers;
    private Integer inactiveMembers;
    private Integer pendingInvitations;
    
    // Member counts by role
    private Integer adminCount;
    private Integer managerCount;
    private Integer memberCount;
    private Integer guestCount;
    
    // Activity metrics
    private LocalDateTime lastMemberJoined;
    private LocalDateTime lastMemberLeft;
    private Integer recentJoinings; // Last 30 days
    private Integer recentLeavings; // Last 30 days
    
    // Engagement metrics
    private Double memberRetentionRate;
    private Double averageMembershipDuration; // in days
    private Integer activeMembersLastWeek;
    private Integer activeMembersLastMonth;
    
    // Capacity metrics
    private Integer maxCapacity;
    private Integer currentCapacity;
    private Integer availableSlots;
    private Double capacityUtilization; // percentage
}