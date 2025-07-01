package com.taskManagement.entity;

public enum NotificationType {
    TASK_ASSIGNED,          // Task assigned to user
    TASK_DUE_SOON,         // Task due date approaching
    TASK_OVERDUE,          // Task is overdue
    TASK_COMPLETED,        // Task marked as completed
    TASK_COMMENT,          // New comment on task
    PROJECT_CREATED,       // New project created
    PROJECT_UPDATED,       // Project details updated
    TEAM_INVITATION,       // Invited to join team
    TEAM_MEMBER_ADDED,     // New member added to team
    TEAM_ROLE_UPDATED,     // Team role changed
    DEPENDENCY_RESOLVED,   // Task dependency completed
    SYSTEM_ANNOUNCEMENT,   // System-wide announcement
    MENTION,               // User mentioned in comment
    FILE_UPLOADED,         // New file attached to task
    DEADLINE_REMINDER      // General deadline reminder


}
