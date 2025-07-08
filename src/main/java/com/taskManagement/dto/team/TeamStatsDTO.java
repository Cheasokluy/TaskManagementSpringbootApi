package com.taskManagement.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatsDTO {
    
    private Long teamId;
    private String teamName;
    
    // Member statistics
    private Integer totalMembers;
    private Integer activeMembers;
    private Integer inactiveMembers;
    private Integer adminCount;
    private Integer managerCount;
    private Integer memberCount;
    
    // Project statistics
    private Integer totalProjects;
    private Integer activeProjects;
    private Integer completedProjects;
    private Integer onHoldProjects;
    
    // Task statistics
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer inProgressTasks;
    private Integer pendingTasks;
    
    // Activity statistics
    private LocalDateTime lastActivity;
    private Integer recentJoinings; // Last 30 days
    private Double membershipRetentionRate;
    
    // Performance metrics
    private Double projectCompletionRate;
    private Double taskCompletionRate;
    private Double averageProjectDuration; // in days
    
    // Capacity metrics
    private Integer maxMembers;
    private Integer availableSlots;
    private Double capacityUtilization; // percentage
}