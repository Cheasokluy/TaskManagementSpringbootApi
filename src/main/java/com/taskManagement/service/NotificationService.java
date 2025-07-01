package com.taskManagement.service;
import com.taskManagement.entity.Notification;
import com.taskManagement.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationService {
    // Basic CRUD operations
    Notification createNotification(Notification notification);

    Optional<Notification> getNotificationById(Long id);

    void deleteNotification(Long id);

    // Notifications by user
    List<Notification> getNotificationsByUserId(Long userId);

    List<Notification> getUnreadNotificationsByUserId(Long userId);

    Page<Notification> getNotificationsByUserId(Long userId, Pageable pageable);

    List<Notification> getReadNotificationsByUserId(Long userId);

    // Notifications by type
    List<Notification> getNotificationsByType(Long userId, NotificationType type);

    List<Notification> getRecentNotificationsByType(NotificationType type, LocalDateTime since);

    // Notification management
    Notification markAsRead(Long notificationId);

    void markAllAsRead(Long userId);

    void markAsUnread(Long notificationId);

    // Notification statistics
    long countUnreadNotifications(Long userId);

    long countNotificationsByType(Long userId, NotificationType type);

    // Notification creation helpers
    void notifyTaskAssigned(Long taskId, Long assigneeId, Long assignerId);

    void notifyTaskDueSoon(Long taskId, Long assigneeId);

    void notifyTaskOverdue(Long taskId, Long assigneeId);

    void notifyTaskCompleted(Long taskId, Long creatorId, Long completedById);

    void notifyNewComment(Long taskId, Long commentAuthorId, List<Long> subscriberIds);

    void notifyProjectCreated(Long projectId, List<Long> teamMemberIds);

    void notifyTeamInvitation(Long teamId, Long invitedUserId, Long inviterUserId);

    void notifyMentioned(Long taskId, Long mentionedUserId, Long mentionerUserId);

    void notifyFileUploaded(Long taskId, Long uploaderId, List<Long> subscriberIds);

    // Notification cleanup
    void deleteExpiredNotifications();

    void deleteReadNotificationsOlderThan(LocalDateTime date);

    // Notification validation
    boolean canUserAccessNotification(Long userId, Long notificationId);

    // Bulk operations
    void markMultipleAsRead(List<Long> notificationIds);

    void deleteMultipleNotifications(List<Long> notificationIds);

}
